package dev.sky.main.manager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabCompletionManager implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("balmanager")) {
            if (args.length == 1) {
                // Suggestions for the first argument (set, add, remove)
                if (sender.hasPermission("skyeconomy.balmanager")) {
                    suggestions.addAll(Arrays.asList("set", "add", "remove", "reset"));
                }
            } else if (args.length == 2) {
                // Suggestions for the second argument (player names)
                if (sender.hasPermission("skyeconomy.balmanager")) {
                    for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                        suggestions.add(player.getName());
                    }
                }
            }
        } else if (command.getName().equalsIgnoreCase("balance") || command.getName().equalsIgnoreCase("pay")) {
            if (args.length == 1 && sender.hasPermission("skyeconomy.balance.others")) {
                // Suggestions for player names
                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    suggestions.add(player.getName());
                }
            }
        } else if (command.getName().equalsIgnoreCase("economy")) {
            if (args.length == 1 && sender.hasPermission("skyeconomy.economy")) {
                suggestions.add("reset");
            }
        }

        return suggestions;
    }
}
