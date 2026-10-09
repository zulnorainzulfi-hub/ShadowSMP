package com.shadowsmp;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SkyWarsManager {
    private final ShadowSMP plugin;
    private final Set<String> activeGames = new HashSet<>();
    private final Map<Player, String> playerGames = new HashMap<>();

    public SkyWarsManager(ShadowSMP plugin) {
        this.plugin = plugin;
    }

    public void joinGame(Player player) {
        if (!plugin.getConfig().getBoolean("skywars.enabled", true)) {
            plugin.sendMessage(player, "&cSkyWars is disabled.");
            return;
        }
        if (playerGames.containsKey(player)) {
            plugin.sendMessage(player, "&cYou are already in a game.");
            return;
        }
        playerGames.put(player, "skywars_game_1");
        activeGames.add("skywars_game_1");
        plugin.sendMessage(player, "&aYou joined SkyWars!");
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
            plugin.getLogger().info("No active SkyWars games.");
        } else {
            plugin.getLogger().info("Active SkyWars games: " + activeGames);
        }
    }

    public void stopAllGames() {
        activeGames.clear();
        playerGames.clear();
    }
}
