package dev.antok.serverselector.command;

import dev.antok.serverselector.Serverselector;
import dev.antok.serverselector.config.Config;
import dev.antok.serverselector.inventory.JoinInventory;
import dev.antok.serverselector.util.ServerStarter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class JoinCommand implements CommandExecutor {

    final JoinInventory joinInventory;
    final Logger logger;
    final Config.ConfigFile configFile;

    public JoinCommand(Logger logger, JoinInventory joinInventory, Config.ConfigFile configFile) {
        this.logger = logger;
        this.joinInventory = joinInventory;
        this.configFile = configFile;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendPlainMessage(this.configFile.messages.notAPlayer);
            return true;
        }

        Player player = (Player) sender;
        joinInventory.openInventory(player);

        return true;
    }
}
