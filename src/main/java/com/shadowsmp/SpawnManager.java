package com.shadowsmp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SpawnManager {
    private final ShadowSMP plugin;

    public SpawnManager(ShadowSMP plugin) {
        this.plugin = plugin;
    }

    public void setSpawn(Location location) {
        World world = location.getWorld();
        if (world == null) {
            plugin.getLogger().warning("Attempted to set spawn with a null world.");
            return;
        }

        plugin.getConfig().set("spawn.world", world.getName());
        plugin.getConfig().set("spawn.x", location.getX());
        plugin.getConfig().set("spawn.y", location.getY());
        plugin.getConfig().set("spawn.z", location.getZ());
        plugin.getConfig().set("spawn.yaw", location.getYaw());
        plugin.getConfig().set("spawn.pitch", location.getPitch());
        plugin.saveConfig();
    }

    public Location getSpawn() {
        String worldName = plugin.getConfig().getString("spawn.world", "world");
        World world = Bukkit.getWorld(worldName);

        if (world == null && !Bukkit.getWorlds().isEmpty()) {
            world = Bukkit.getWorlds().get(0);
        }

        if (world == null) {
            return null;
        }

        double x = plugin.getConfig().getDouble("spawn.x", world.getSpawnLocation().getX());
        double y = plugin.getConfig().getDouble("spawn.y", world.getSpawnLocation().getY());
        double z = plugin.getConfig().getDouble("spawn.z", world.getSpawnLocation().getZ());
        float yaw = (float) plugin.getConfig().getDouble("spawn.yaw", world.getSpawnLocation().getYaw());
        float pitch = (float) plugin.getConfig().getDouble("spawn.pitch", world.getSpawnLocation().getPitch());

        return new Location(world, x, y, z, yaw, pitch);
    }

    public void teleportToSpawn(Player player) {
        Location spawn = getSpawn();
        if (spawn == null) {
            plugin.sendMessage(player, "&cThe server spawn is not available.");
            return;
        }
        player.teleport(spawn);
        plugin.sendMessage(player, "&aTeleported to spawn.");
    }

    public void loadSpawnFromConfig() {
        if (!plugin.getConfig().contains("spawn.world")) {
            if (!Bukkit.getWorlds().isEmpty()) {
                World world = Bukkit.getWorlds().get(0);
                plugin.getConfig().set("spawn.world", world.getName());
                plugin.getConfig().set("spawn.x", world.getSpawnLocation().getX());
                plugin.getConfig().set("spawn.y", world.getSpawnLocation().getY());
                plugin.getConfig().set("spawn.z", world.getSpawnLocation().getZ());
                plugin.getConfig().set("spawn.yaw", world.getSpawnLocation().getYaw());
                plugin.getConfig().set("spawn.pitch", world.getSpawnLocation().getPitch());
                plugin.saveConfig();
            }
        }
    }
}
