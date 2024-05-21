package dev.sky.main.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.sky.main.manager.BalanceLimitManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class DataManager {
    private final JavaPlugin plugin;
    private final Gson gson;
    private final File dataFolder;
    private final BalanceLimitManager balanceLimitManager;

    public DataManager(JavaPlugin plugin, BalanceLimitManager balanceLimitManager) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.dataFolder = new File(plugin.getDataFolder(), "data");
        this.balanceLimitManager = balanceLimitManager;

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public void createPlayerData(UUID playerUUID) {
        File playerDataFile = new File(dataFolder, playerUUID.toString() + ".json");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
                plugin.getLogger().info("Created data file for player " + playerUUID.toString());
            } catch (IOException e) {
                plugin.getLogger().warning("An error occurred while creating data file for player " + playerUUID.toString() + ": " + e.getMessage());
            }
        }
    }

    public double getPlayerBalance(UUID playerUUID) {
        File playerDataFile = new File(dataFolder, playerUUID.toString() + ".json");
        if (playerDataFile.exists()) {
            try (FileReader reader = new FileReader(playerDataFile)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                if (jsonObject.has("balance")) {
                    return jsonObject.get("balance").getAsDouble();
                }
            } catch (IOException e) {
                plugin.getLogger().warning("An error occurred while reading data for player " + playerUUID.toString() + ": " + e.getMessage());
            }
        }
        return 0;
    }

    public void setPlayerBalance(UUID playerUUID, double amount) {
        if (!balanceLimitManager.isWithinLimit(amount)) {
            plugin.getLogger().warning("Attempted to set balance above limit for player " + playerUUID.toString());
            return;
        }

        File playerDataFile = new File(dataFolder, playerUUID.toString() + ".json");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("balance", amount);
        try (FileWriter writer = new FileWriter(playerDataFile)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("An error occurred while writing data for player " + playerUUID.toString() + ": " + e.getMessage());
        }
    }

    public void deleteAllDataFiles() {
        File dataFolder = plugin.getDataFolder();
        File[] files = dataFolder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }
}