package dev.antok.serverselector.util;

import dev.antok.serverselector.Serverselector;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendPlayerToServer {
    public static void sendPlayerToServer(Player player, String server, Serverselector instance) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        player.sendPluginMessage(instance, "BungeeCord", b.toByteArray());
    }
}
