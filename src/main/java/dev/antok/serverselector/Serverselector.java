package dev.antok.serverselector;

import dev.antok.serverselector.command.JoinCommand;
import dev.antok.serverselector.config.Config;
import dev.antok.serverselector.inventory.JoinInventory;
import dev.antok.serverselector.config.ConfigManager;
import dev.antok.serverselector.util.SendPlayerToServer;
import dev.antok.serverselector.util.ServerStarter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Logger;

public final class Serverselector extends JavaPlugin {
    public HashMap<Player, Integer> joiningPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        Logger logger = getLogger();

        final Serverselector instance = this;

        Config.ConfigFile configFile = new ConfigManager(this).configFile;
        MiniMessage mm = MiniMessage.miniMessage();

        final ServerStarter serverStarter = new ServerStarter(logger, configFile);
        final JoinInventory joinInventory = new JoinInventory(logger, serverStarter, this, configFile);

        this.getServer().getPluginManager().registerEvents(joinInventory, this);
        this.getCommand("join").setExecutor(new JoinCommand(logger, joinInventory, configFile));
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            HashMap<Integer, Boolean> statusCache = new HashMap<>();
            for (Player player : joiningPlayers.keySet()) {
                final Integer serverId = joiningPlayers.get(player);

                Config.Item server = configFile.server.stream().filter(item -> item.serverId == serverId).findFirst().orElse(null);
                if (server == null) {
                    player.sendMessage(configFile.messages.noSuchServer);
                    logger.severe(String.format("No server with id %d found", serverId));
                    joiningPlayers.remove(player);
                    continue;
                }

                if (statusCache.containsKey(serverId)) {
                    if (statusCache.get(serverId)) {
                        joiningPlayers.remove(player);
                        SendPlayerToServer.sendPlayerToServer(player, server.serverName, instance);
                    } else {
                        player.sendMessage(configFile.messages.waitingForServer);
                        continue;
                    }
                }

                try {
                    boolean status = serverStarter.getServerStatus(serverId);
                    statusCache.put(serverId, status);

                    if (status) {
                        joiningPlayers.remove(player);
                        if (server.serverName == null) {
                            player.sendMessage(configFile.messages.noSuchServer);
                            continue;
                        }
                        SendPlayerToServer.sendPlayerToServer(player, server.serverName, instance);
                    } else {
                        player.sendMessage(configFile.messages.waitingForServer);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


            }
        }, 20 * 5, 20 * 5);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
