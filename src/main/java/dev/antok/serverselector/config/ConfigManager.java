package dev.antok.serverselector.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import dev.antok.serverselector.Serverselector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ConfigManager {

    public Config.ConfigFile configFile;
    public ConfigManager(Serverselector main) {
        final Logger logger = main.getLogger();
        final File configFile = Paths.get(main.getDataFolder().getPath(), "config.toml").toFile();

        if (!configFile.exists()) {
            logger.info("No configuration found. Creating...");

            TomlWriter tomlWriter = new TomlWriter.Builder().build();

            main.getDataFolder().mkdirs();

            try {
                tomlWriter.write(createNewConfig(), configFile);
                logger.info("Configuration created successfully!");
            } catch (IOException e) {
                logger.severe(e.getMessage());
                return;
            }
        }

        logger.info("Loading configuration...");
        this.configFile = new Toml().read(configFile).to(Config.ConfigFile.class);
        if (this.configFile == null) {
            logger.severe("Failed to load configuration!");
            return;
        }

        logger.info("Loaded configuration successfully!");
    }

    Config.ConfigFile createNewConfig() {
        Config config = new Config();
        List<Config.Item> items = Arrays.asList(
                config.createItem("<bold>Example server 1</bold>", 2, "DIRT", Arrays.asList("Join now", "Line 2"), 2, "server_1"),
                config.createItem("<bold>Example server 2</bold>", 6, "DIAMOND", Arrays.asList("Join now", "Line 2"), 3, "server_2")
        );

        Config.Messages messages = config.createMessages(
                "<red>You can only run this as a player</red>",
                "<red>No server with ID</red>",
                "Sending to server",
                "Starting the server",
                "Waiting for the server to start",
                "<red>Could not start server</red>"
        );

        return config.createConfigFile(9, "Select a server", "https://example.com", "admin", "crafty", items, messages);
    }
}
