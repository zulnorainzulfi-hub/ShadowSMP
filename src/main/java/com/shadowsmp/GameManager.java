package com.shadowsmp;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class GameManager {
    private final ShadowSMP plugin;
    private final Set<Player> inGamePlayers = new HashSet<>();

    public GameManager(ShadowSMP plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        inGamePlayers.add(player);
    }

    public void removePlayer(Player player) {
        inGamePlayers.remove(player);
    }

    public boolean isInGame(Player player) {
        return inGamePlayers.contains(player);
    }

    public Set<Player> getGamePlayers() {
        return new HashSet<>(inGamePlayers);
    }

    public void openGameMenu(Player player) {
        plugin.sendMessage(player, "&aGame menu opened. Choose a game mode!");
    }
}
