package dev.antok.serverselector.inventory;

import dev.antok.serverselector.Serverselector;
import dev.antok.serverselector.config.Config;
import dev.antok.serverselector.util.SendPlayerToServer;
import dev.antok.serverselector.util.ServerStarter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.parser.ParseException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class JoinInventory implements Listener {
    private final Inventory inventory;
    private final Logger logger;
    private final ServerStarter serverStarter;
    private final Serverselector main;
    final Config.ConfigFile configFile;


    public JoinInventory(Logger logger, ServerStarter serverStarter, Serverselector serverselector, Config.ConfigFile configFile) {
        this.logger = logger;
        this.serverStarter = serverStarter;
        this.main = serverselector;
        this.configFile = configFile;

        inventory = Bukkit.createInventory(null, 9, "Example");

        initializeItems();
    }

    public void initializeItems() {
        for (Config.Item server : configFile.server) {
            inventory.setItem(server.slot, createGuiItem(Material.getMaterial(server.material), server.name, server.lore));
        }
    }

    public void openInventory(final HumanEntity entity) {
        entity.openInventory(inventory);
    }

    protected ItemStack createGuiItem(final Material material, final String name, final List<String> lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        event.setCancelled(true);

        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player player = (Player) event.getWhoClicked();

        Config.Item server = configFile.server.stream().filter(item -> item.slot == event.getRawSlot()).findFirst().orElse(null);
        if (server == null) return;
        int serverID = server.serverId;


        boolean isServerRunning;
        try {
            isServerRunning = serverStarter.getServerStatus(serverID);
        } catch (NoSuchAlgorithmException | KeyManagementException | ExecutionException | InterruptedException |
                 ParseException e) {
            logger.severe(e.getMessage());
            return;
        } catch (Exception e) {
            player.sendMessage(e.getMessage());
            logger.severe(e.getMessage());
            return;
        }

        if (isServerRunning) {
            final String serverName = server.serverName;

            player.sendMessage(configFile.messages.sendingToServer);
            SendPlayerToServer.sendPlayerToServer(player, serverName, main);
        } else {
            player.sendMessage(configFile.messages.startingServer);
            try {
                serverStarter.requestServerStart(serverID);
            } catch (NoSuchAlgorithmException | KeyManagementException | ExecutionException | InterruptedException e) {
                logger.severe(e.getMessage());
                player.sendMessage(configFile.messages.serverStartError);
            }
            main.joiningPlayers.put(player, serverID);
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(final PlayerQuitEvent e) {

    }
}
