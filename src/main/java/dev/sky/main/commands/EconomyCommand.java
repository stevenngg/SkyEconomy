package dev.sky.main.commands;

import dev.sky.main.SkyEconomy;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyCommand implements CommandExecutor {
    private final SkyEconomy plugin;
    private final MiniMessage miniMessage;

    public EconomyCommand(SkyEconomy plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("skyeconomy.economy")) {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "reset":
                        handleResetCommand(sender);
                        break;
                    default:
                        sender.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("economy-usage", "")));
                        break;
                }
            } else {
                sender.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("economy-usage", "")));
            }
        } else {
            sender.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("no-permissions", "<red>You don't have permissions.")));
        }
        return true;
    }

    private void handleResetCommand(CommandSender sender) {
        if (sender instanceof Player) {
            plugin.getManager().getDataManager().deleteAllDataFiles();
            sender.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("economy-successfully", "")));
        } else {
            sender.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("player-command", "<red>This command can only be executed by players.")));
        }
    }
}