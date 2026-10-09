package com.shadowsmp;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BedWarsManager {
    private final ShadowSMP plugin;
    private final Set<String> activeGames = new HashSet<>();
    private final Map<Player, String> playerGames = new HashMap<>();

    public BedWarsManager(ShadowSMP plugin) {
        this.plugin = plugin;
    }

    public void joinGame(Player player) {
        if (!plugin.getConfig().getBoolean("bedwars.enabled", true)) {
            plugin.sendMessage(player, "&cBedWars is disabled.");
            return;
        }
        if (playerGames.containsKey(player)) {
            plugin.sendMessage(player, "&cYou are already in a game.");
            return;
        }
        playerGames.put(player, "bedwars_game_1");
        activeGames.add("bedwars_game_1");
        plugin.sendMessage(player, "&aYou joined BedWars!");
    }

    public void leaveGame(Player player) {
        if (!playerGames.containsKey(player)) {
            plugin.sendMessage(player, "&cYou are not in a game.");
            return;
        }
        String game = playerGames.remove(player);
        plugin.sendMessage(player, "&aYou left " + game);
    }

    public void listGames() {
        if (activeGames.isEmpty()) {
            plugin.getLogger().info("No active BedWars games.");
        } else {
            plugin.getLogger().info("Active BedWars games: " + activeGames);
        }
    }

    public void stopAllGames() {
        activeGames.clear();
        playerGames.clear();
    }
}
