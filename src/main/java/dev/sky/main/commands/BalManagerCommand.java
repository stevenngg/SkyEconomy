package dev.sky.main.commands;

import dev.sky.main.SkyEconomy;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class BalManagerCommand implements CommandExecutor {
    private final SkyEconomy plugin;
    private final MiniMessage miniMessage;

    public BalManagerCommand(SkyEconomy plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("skyeconomy.balmanager")) {
                if (args.length == 2) {
                    String subCommand = args[0].toLowerCase();
                    String playerName = args[1];

                    OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(playerName);
                    if (targetPlayer.hasPlayedBefore() || targetPlayer.isOnline()) {
                        UUID playerUUID = targetPlayer.getUniqueId();

                        switch (subCommand) {
                            case "set":
                                handleSetCommand(player, playerUUID, Double.parseDouble(args[2]), playerName);
                                break;
                            case "add":
                                handleAddCommand(player, playerUUID, Double.parseDouble(args[2]), playerName);
                                break;
                            case "remove":
                                handleRemoveCommand(player, playerUUID, Double.parseDouble(args[2]), playerName);
                                break;
                            case "reset":
                                handleResetCommand(player, playerUUID, playerName);
                                break;
                            default:
                                player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("balmanager-usage", "<red>Usage: /balmanager <set|add|remove|reset> <player> <amount>")));
                                break;
                        }
                    } else {
                        player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("player-not-found", "<red>Player not found.")));
                    }
                } else {
                    player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("balmanager-usage", "<red>Usage: /balmanager <set|add|remove|reset> <player> <amount>")));
                }
            } else {
                player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("no-permissions", "<red>You don't have permissions.")));
            }
        } else {
            sender.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("player-command", "<red>This command can only be executed by players.")));
        }
        return true;
    }

    private void handleSetCommand(Player player, UUID playerUUID, double amount, String playerName) {
        double balanceLimit = plugin.getManager().getBalanceLimitManager().getBalanceLimit();
        if (balanceLimit != -1 && amount > balanceLimit) {
            player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("balmanager-set-limit", "<red>Cannot set balance. The amount exceeds the limit of $" + balanceLimit).replace("{limit}", String.valueOf(balanceLimit))));
            return;
        }

        plugin.getManager().getDataManager().setPlayerBalance(playerUUID, amount);
        player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("balmanager-set-balance", "").replace("{playerName}", playerName).replace("{amount}", String.valueOf(amount))));
    }

    private void handleAddCommand(Player player, UUID playerUUID, double amount, String playerName) {
        double currentBalance = plugin.getManager().getDataManager().getPlayerBalance(playerUUID);
        double newBalance = currentBalance + amount;

        double balanceLimit = plugin.getManager().getBalanceLimitManager().getBalanceLimit();
        if (balanceLimit != -1 && newBalance > balanceLimit) {
            player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("balmanager-add-limit", "").replace("{limit}", String.valueOf(balanceLimit))));
            return;
        }

        plugin.getManager().getDataManager().setPlayerBalance(playerUUID, newBalance);
        player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("balmanager-add-balance", "").replace("{amount}", String.valueOf(amount)).replace("{playerName}", playerName).replace("{newBalance}", String.valueOf(newBalance))));
    }

    private void handleRemoveCommand(Player player, UUID playerUUID, double amount, String playerName) {
        double currentBalance = plugin.getManager().getDataManager().getPlayerBalance(playerUUID);
        double newBalance = currentBalance - amount;

        if (newBalance < 0) {
            player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("balmanager-remove-limit", "")));
            return;
        }

        plugin.getManager().getDataManager().setPlayerBalance(playerUUID, newBalance);
        player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("balmanager-remove-balance", "").replace("{amount}", String.valueOf(amount)).replace("{playerName}", playerName).replace("{newBalance}", String.valueOf(newBalance))));
    }

    private void handleResetCommand(Player player, UUID playerUUID, String playerName) {
        File playerDataFile = new File(plugin.getDataFolder(), playerUUID.toString() + ".json");
        if (playerDataFile.exists()) {
            if (playerDataFile.delete()) {
                player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("balmanager-reset-successfully", "").replace("{playerName}", playerName)));
            } else {
                player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("balmanager-reset-failed", "").replace("{playerName}", playerName)));
            }
        } else {
            player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("balmanager-reset-not-found", "").replace("{playerName}", playerName)));
        }
    }
}