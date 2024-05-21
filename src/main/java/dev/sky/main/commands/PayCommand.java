package dev.sky.main.commands;

import dev.sky.main.SkyEconomy;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PayCommand implements CommandExecutor {
    private final SkyEconomy plugin;
    private final MiniMessage miniMessage;

    public PayCommand(SkyEconomy plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length >= 2) {
                String targetName = args[0];
                double amount;

                try {
                    if (args[1].equalsIgnoreCase("all")) {
                        amount = plugin.getManager().getDataManager().getPlayerBalance(player.getUniqueId());
                    } else {
                        amount = Double.parseDouble(args[1]);
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("invalid-amount", "")));
                    return true;
                }

                if (amount <= 0) {
                    player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("amount-positive", "")));
                    return true;
                }

                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetName);
                if (targetPlayer.hasPlayedBefore() || targetPlayer.isOnline()) {
                    UUID targetUUID = targetPlayer.getUniqueId();
                    UUID playerUUID = player.getUniqueId();

                    double senderBalance = plugin.getManager().getDataManager().getPlayerBalance(playerUUID);

                    if (senderBalance < amount) {
                        player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("enough-money", "")));
                        return true;
                    }

                    double newSenderBalance = senderBalance - amount;
                    plugin.getManager().getDataManager().setPlayerBalance(playerUUID, newSenderBalance);

                    double receiverBalance = plugin.getManager().getDataManager().getPlayerBalance(targetUUID);
                    double newReceiverBalance = receiverBalance + amount;
                    double balanceLimit = plugin.getManager().getBalanceLimitManager().getBalanceLimit();

                    if (balanceLimit != -1 && newReceiverBalance > balanceLimit) {
                        player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("pay-limit", "").replace("{limit}", String.valueOf(balanceLimit))));
                        plugin.getManager().getDataManager().setPlayerBalance(playerUUID, senderBalance); // Revert the transaction
                        return true;
                    }

                    plugin.getManager().getDataManager().setPlayerBalance(targetUUID, newReceiverBalance);

                    player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("pay-player", "").replace("{amount}", String.valueOf(amount)).replace("{targetName}", targetName).replace("{newBalance}", String.valueOf(newSenderBalance))));
                    if (targetPlayer.isOnline()) {
                        ((Player) targetPlayer).sendMessage(miniMessage.deserialize(plugin.getConfig().getString("receiver-pay", "").replace("{amount}", String.valueOf(amount)).replace("{playerName}", player.getName()).replace("{newReceiverBalance}", String.valueOf(newReceiverBalance))));
                    }
                } else {
                    player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("player-not-found", "")));
                }
            } else {
                player.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("pay-usage", "")));
            }
        } else {
            sender.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("player-command", "<red>This command can only be executed by players.")));
        }
        return true;
    }
}