package com.shadowsmp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private final ShadowSMP plugin;
    private final File dataFile;
    private FileConfiguration config;

    public PlayerDataManager(ShadowSMP plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        this.config = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void load() {
        if (!dataFile.exists()) {
            try {
                if (!plugin.getDataFolder().exists()) {
                    plugin.getDataFolder().mkdirs();
                }
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create player data file: " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void save() {
        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save player data: " + e.getMessage());
        }
    }

    public void ensurePlayer(UUID uuid) {
        String path = "players." + uuid.toString();
        if (!config.contains(path)) {
            config.createSection(path);
        }
        if (!config.isConfigurationSection(path + ".homes")) {
            config.createSection(path + ".homes");
        }
        if (!config.contains(path + ".tokens")) {
            config.set(path + ".tokens", plugin.getConfig().getInt("economy.starting-tokens", 100));
        }
        save();
    }

    public int getTokens(UUID uuid) {
        ensurePlayer(uuid);
        return Math.max(0, config.getInt("players." + uuid + ".tokens", plugin.getConfig().getInt("economy.starting-tokens", 100)));
    }

    public void setTokens(UUID uuid, int amount) {
        ensurePlayer(uuid);
        config.set("players." + uuid + ".tokens", Math.max(0, amount));
        save();
    }

    public void addTokens(UUID uuid, int amount) {
        setTokens(uuid, getTokens(uuid) + amount);
    }

    public void removeTokens(UUID uuid, int amount) {
        setTokens(uuid, getTokens(uuid) - amount);
    }

    public void setHome(UUID uuid, String name, Location location) {
        ensurePlayer(uuid);
        String path = "players." + uuid + ".homes." + name.toLowerCase();
        config.set(path + ".world", location.getWorld() != null ? location.getWorld().getName() : "world");
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        config.set(path + ".yaw", location.getYaw());
        config.set(path + ".pitch", location.getPitch());
        save();
    }

    public Location getHome(UUID uuid, String name) {
        ensurePlayer(uuid);
        String path = "players." + uuid + ".homes." + name.toLowerCase();
        if (!config.contains(path + ".world")) {
            return null;
        }

        String worldName = config.getString(path + ".world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }

        return new Location(
                world,
                config.getDouble(path + ".x"),
                config.getDouble(path + ".y"),
                config.getDouble(path + ".z"),
                (float) config.getDouble(path + ".yaw", 0.0D),
                (float) config.getDouble(path + ".pitch", 0.0D)
        );
    }

    public boolean hasHome(UUID uuid, String name) {
        ensurePlayer(uuid);
        return config.contains("players." + uuid + ".homes." + name.toLowerCase() + ".world");
    }

    public void removeHome(UUID uuid, String name) {
        ensurePlayer(uuid);
        config.set("players." + uuid + ".homes." + name.toLowerCase(), null);
        save();
    }

    public Map<String, Location> getHomes(UUID uuid) {
        ensurePlayer(uuid);
        Map<String, Location> homes = new HashMap<>();
        if (!config.isConfigurationSection("players." + uuid + ".homes")) {
            return homes;
        }

        for (String key : config.getConfigurationSection("players." + uuid + ".homes").getKeys(false)) {
            Location home = getHome(uuid, key);
            if (home != null) {
                homes.put(key, home);
            }
        }

        return homes;
    }

    public void setLastMessenger(UUID receiver, UUID sender) {
        ensurePlayer(receiver);
        config.set("players." + receiver + ".lastMessenger", sender.toString());
        save();
    }

    public UUID getLastMessenger(UUID receiver) {
        ensurePlayer(receiver);
        String id = config.getString("players." + receiver + ".lastMessenger");
        if (id == null || id.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void clearLastMessenger(UUID receiver) {
        ensurePlayer(receiver);
        config.set("players." + receiver + ".lastMessenger", null);
        save();
    }

    public void onPlayerJoin(Player player) {
        ensurePlayer(player.getUniqueId());
    }
}
