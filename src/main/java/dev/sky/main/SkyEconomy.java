package dev.sky.main;

import dev.sky.main.api.SkyEconomyAPI;
import dev.sky.main.commands.BalManagerCommand;
import dev.sky.main.commands.BalanceCommand;
import dev.sky.main.commands.EconomyCommand;
import dev.sky.main.commands.PayCommand;
import dev.sky.main.config.ConfigManager;
import dev.sky.main.listeners.PlayerJoinListener;
import dev.sky.main.manager.TabCompletionManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkyEconomy extends JavaPlugin {

    private ConfigManager configManager;
    private SkyEconomyAPI api;

    @Override
    public void onEnable() {
        // Plugin startup logic
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Register plugin API
        this.api = new SkyEconomyAPI(this);

        // Register commands
        this.getCommand("balance").setExecutor(new BalanceCommand(this));
        this.getCommand("balmanager").setExecutor(new BalManagerCommand(this));
        this.getCommand("pay").setExecutor(new PayCommand(this));
        this.getCommand("economy").setExecutor(new EconomyCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Register tab completer
        this.getCommand("balance").setTabCompleter(new TabCompletionManager());
        this.getCommand("balmanager").setTabCompleter(new TabCompletionManager());
        this.getCommand("pay").setTabCompleter(new TabCompletionManager());
        this.getCommand("economy").setTabCompleter(new TabCompletionManager());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        configManager.saveConfig();
    }

    public ConfigManager getManager() {
        return configManager;
    }
    public SkyEconomyAPI getAPI() {
        return api;
    }
}