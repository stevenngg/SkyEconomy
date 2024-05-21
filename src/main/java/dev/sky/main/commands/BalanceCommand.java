package dev.sky.main.commands;

import dev.sky.main.SkyEconomy;
import dev.sky.main.data.DataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BalanceCommand implements CommandExecutor {
    private final SkyEconomy plugin;
    private final MiniMessage miniMessage;

    public BalanceCommand(SkyEconomy plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                UUID playerUUID = player.getUniqueId();
                DataManager dataManager = plugin.getManager().getDataManager();
                double balance = dataManager.getPlayerBalance(playerUUID);

                Component message = miniMessage.deserialize(plugin.getConfig().getString("player-balance", "<green>Your balance is: ${bal}").replace("{bal}", String.valueOf(balance)));
                player.sendMessage(message);
            } else {
                sender.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("player-command", "<red>This command can only be executed by players.")));
            }
        } else if (args.length == 1) {
            if (sender.hasPermission("skyeconomy.balance.others")) {
                String playerName = args[0];
                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(playerName);
                if (targetPlayer.hasPlayedBefore() || targetPlayer.isOnline()) {
                    UUID playerUUID = targetPlayer.getUniqueId();
                    DataManager dataManager = plugin.getManager().getDataManager();
                    double balance = dataManager.getPlayerBalance(playerUUID);

                    Component message = miniMessage.deserialize(plugin.getConfig().getString("other-balance", "<green>Balance of player {playerName} is: ${bal}").replace("{bal}", String.valueOf(balance)).replace("{playerName}", playerName));
                    sender.sendMessage(message);
                } else {
                    sender.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("player-not-found", "<red>Player not found.")));
                }
            } else {
                sender.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("no-permissions", "<red>You don't have permissions.")));
            }
        } else {
            sender.sendMessage(miniMessage.deserialize(plugin.getConfig().getString("balance-usage", "<red>Usage: /balance [player]")));
        }
        return true;
    }
}