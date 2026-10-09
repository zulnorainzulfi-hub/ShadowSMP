package com.shadowsmp;

import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class VehicleManager {
    private final ShadowSMP plugin;
    private final Map<Player, String> playerVehicles = new HashMap<>();
    private final java.util.List<String> vehicleTypes = java.util.Arrays.asList("car", "bike", "helicopter");

    public VehicleManager(ShadowSMP plugin) {
        this.plugin = plugin;
    }

    public void spawnVehicle(Player player, String type) {
        if (!plugin.getConfig().getBoolean("vehicles.enabled", true)) {
            plugin.sendMessage(player, "&cVehicles are disabled.");
            return;
        }
        if (!vehicleTypes.contains(type.toLowerCase())) {
            plugin.sendMessage(player, "&cUnknown vehicle type. Available: car, bike, helicopter");
            return;
        }
        if (playerVehicles.containsKey(player)) {
            plugin.sendMessage(player, "&cYou already have a vehicle. Despawn it first.");
            return;
        }
        Location loc = player.getLocation();
        ArmorStand stand = player.getWorld().spawn(loc, ArmorStand.class);
        stand.setCustomName(type.toUpperCase());
        stand.setCustomNameVisible(true);
        stand.setMarker(true);
        playerVehicles.put(player, type);
        plugin.sendMessage(player, "&aVehicle spawned: " + type);
    }

    public void despawnVehicle(Player player) {
        if (playerVehicles.containsKey(player)) {
            playerVehicles.remove(player);
            plugin.sendMessage(player, "&aVehicle despawned.");
        } else {
            plugin.sendMessage(player, "&cYou don't have a vehicle spawned.");
        }
    }

    public void listVehicles() {
        plugin.getLogger().info("Available vehicles: car, bike, helicopter");
    }
}
