package dev.sky.main.config;

import dev.sky.main.manager.BalanceLimitManager;
import dev.sky.main.data.DataManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final JavaPlugin plugin;
    private File settingsFile;
    private FileConfiguration settings;
    private DataManager dataManager;
    private BalanceLimitManager balanceLimitManager;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.balanceLimitManager = new BalanceLimitManager(plugin);
        this.dataManager = new DataManager(plugin, balanceLimitManager);
    }

    public void loadConfig() {
        settingsFile = new File(plugin.getDataFolder(), "config.yml");

        if (!settingsFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        settings = YamlConfiguration.loadConfiguration(settingsFile);
    }

    public FileConfiguration getSettings() {
        return settings;
    }

    public void saveConfig() {
        try {
            settings.save(settingsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        settingsFile = new File(plugin.getDataFolder(), "config.yml");
        settings = YamlConfiguration.loadConfiguration(settingsFile);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public BalanceLimitManager getBalanceLimitManager() {
        return balanceLimitManager;
    }
}