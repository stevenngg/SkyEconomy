package dev.sky.main.listeners;

import dev.sky.main.SkyEconomy;
import dev.sky.main.config.ConfigManager;
import dev.sky.main.data.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final SkyEconomy plugin;
    private final ConfigManager configManager;
    private final DataManager dataManager;

    public PlayerJoinListener(SkyEconomy plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getManager();
        this.dataManager = configManager.getDataManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (!player.hasPlayedBefore()) {
            dataManager.createPlayerData(playerUUID);
            double startingBalance = configManager.getSettings().getDouble("starting-balance");
            dataManager.setPlayerBalance(playerUUID, startingBalance);
        }
    }
}