package dev.sky.main.manager;

import org.bukkit.plugin.java.JavaPlugin;

public class BalanceLimitManager {
    private final JavaPlugin plugin;
    private double balanceLimit;

    public BalanceLimitManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadBalanceLimit();
    }

    private void loadBalanceLimit() {
        this.balanceLimit = plugin.getConfig().getDouble("balance-limit", -1);
    }

    public double getBalanceLimit() {
        return balanceLimit;
    }

    public boolean isWithinLimit(double amount) {
        return balanceLimit == -1 || amount <= balanceLimit;
    }
}