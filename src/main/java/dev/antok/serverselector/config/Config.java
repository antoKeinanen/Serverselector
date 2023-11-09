package dev.antok.serverselector.config;

import java.util.List;

public class Config {
    public Item createItem(String name, int slot, String material, List<String> lore, int serverId, String serverName) {
        return new Item(name, slot, material, lore, serverId, serverName);
    }

    public Messages createMessages(String notAPlayer, String noSuchServer, String sendingToServer, String startingServer, String waitingForServer, String serverStartError) {
        return new Messages(notAPlayer, noSuchServer, sendingToServer, startingServer, waitingForServer, serverStartError);
    }

    public ConfigFile createConfigFile(int inventorySize, String inventoryName, String panelUrl, String username, String password, List<Item> servers, Messages messages) {
        return new ConfigFile(inventorySize, inventoryName, panelUrl, username, password, servers, messages);
    }

    public class ConfigFile {
        public int inventorySize;
        public String inventoryName;
        public String panelUrl;
        public String username;
        public String password;
        public List<Item> server;
        public Messages messages;

        public ConfigFile(int inventorySize, String inventoryName, String panelUrl, String username, String password, List<Item> server, Messages messages) {
            this.inventorySize = inventorySize;
            this.inventoryName = inventoryName;
            this.panelUrl = panelUrl;
            this.username = username;
            this.password = password;
            this.server = server;
            this.messages = messages;
        }
    }

    public class Item {
        public String name;
        public int slot;
        public String material;
        public List<String> lore;
        public int serverId;
        public String serverName;

        public Item(String name, int slot, String material, List<String> lore, int serverId, String serverName) {
            this.name = name;
            this.slot = slot;
            this.material = material;
            this.lore = lore;
            this.serverId = serverId;
            this.serverName = serverName;
        }
    }

    public class Messages {
        public String notAPlayer;
        public String noSuchServer;
        public String sendingToServer;
        public String startingServer;
        public String waitingForServer;
        public String serverStartError;

        public Messages(String notAPlayer, String noSuchServer, String sendingToServer, String startingServer, String waitingForServer, String serverStartError) {
            this.notAPlayer = notAPlayer;
            this.noSuchServer = noSuchServer;
            this.sendingToServer = sendingToServer;
            this.startingServer = startingServer;
            this.waitingForServer = waitingForServer;
            this.serverStartError = serverStartError;
        }
    }
}
