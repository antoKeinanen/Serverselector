package dev.antok.serverselector.util;


import dev.antok.serverselector.config.Config;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerStarter {
    private static final TrustManager MOCK_TRUST_MANAGER = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    final Logger logger;
    Config.ConfigFile configFile;
    private String token;


    public ServerStarter(Logger logger, Config.ConfigFile configFile) {
        this.logger = logger;
        this.configFile = configFile;

        try {
            authenticate();
        } catch (ExecutionException | InterruptedException | NoSuchAlgorithmException | KeyManagementException |
                 ParseException e) {
            logger.severe(e.getMessage());
        }
    }

    private void authenticate() throws ExecutionException, InterruptedException, NoSuchAlgorithmException, KeyManagementException, ParseException {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{MOCK_TRUST_MANAGER}, new SecureRandom());
        HttpClient client = HttpClient.newBuilder().sslContext(sslContext).build();

        JSONObject requestBody = new JSONObject();
        requestBody.put("username", configFile.username);
        requestBody.put("password", configFile.password);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(configFile.panelUrl + "/api/v2/auth/login")).POST(HttpRequest.BodyPublishers.ofString(requestBody.toJSONString())).build();

        CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = futureResponse.get();


        if (response.statusCode() != 200) {
            logger.severe("Failed to authenticate: ");
            logger.severe(response.toString());
            logger.severe(response.body());
            return;
        }

        JSONObject responseObject = (JSONObject) new JSONParser().parse(response.body());
        JSONObject responseData = (JSONObject) responseObject.get("data");
        String token = (String) responseData.get("token");

        if (token == null) {
            logger.severe("Failed to get token");
        } else {
            this.token = token;
        }
    }

    public void requestServerStart(int serverId) throws NoSuchAlgorithmException, KeyManagementException, ExecutionException, InterruptedException {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{MOCK_TRUST_MANAGER}, new SecureRandom());
        HttpClient client = HttpClient.newBuilder().sslContext(sslContext).build();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(String.format(configFile.panelUrl + "/api/v2/servers/%d/action/start_server", serverId))).header("Authorization", "Bearer " + this.token).POST(HttpRequest.BodyPublishers.noBody()).build();

        CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = futureResponse.get();


        if (response.statusCode() != 200) {
            logger.severe("Failed to request server start: ");
            logger.severe(response.toString());
            return;
        }

        logger.info("Ok " + response.body());
    }

    public boolean getServerStatus(int serverID) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{MOCK_TRUST_MANAGER}, new SecureRandom());
        HttpClient client = HttpClient.newBuilder().sslContext(sslContext).build();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(configFile.panelUrl + "/api/v2/servers/" + serverID + "/stats")).header("Authorization", "Bearer " + this.token).GET().build();

        CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = futureResponse.get();


        if (response.statusCode() != 200) {
            throw new Exception(String.format("Failed to get status of server '%d' error code '%d'", serverID, response.statusCode()));
        }

        JSONObject responseObject = (JSONObject) new JSONParser().parse(response.body());
        JSONObject responseData = (JSONObject) responseObject.get("data");
        return !responseData.get("players").equals("False");
    }
}
