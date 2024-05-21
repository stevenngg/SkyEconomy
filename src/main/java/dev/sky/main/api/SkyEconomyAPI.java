package dev.sky.main.api;

import dev.sky.main.SkyEconomy;
import dev.sky.main.data.DataManager;

import java.util.UUID;

public class SkyEconomyAPI {
    private final SkyEconomy plugin;
    private final DataManager dataManager;

    public SkyEconomyAPI(SkyEconomy plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getManager().getDataManager();
    }

    public double getBalance(UUID playerUUID) {
        return dataManager.getPlayerBalance(playerUUID);
    }

    public void addBalance(UUID playerUUID, double amount) {
        double currentBalance = getBalance(playerUUID);
        dataManager.setPlayerBalance(playerUUID, currentBalance + amount);
    }

    public void removeBalance(UUID playerUUID, double amount) {
        double currentBalance = getBalance(playerUUID);
        dataManager.setPlayerBalance(playerUUID, currentBalance - amount);
    }

    public void setBalance(UUID playerUUID, double amount) {
        dataManager.setPlayerBalance(playerUUID, amount);
    }
}