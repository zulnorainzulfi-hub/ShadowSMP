package com.shadowsmp;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDeathEvent;
import org.bukkit.entity.Player;

public class GameListener implements Listener {
    private final ShadowSMP plugin;

    public GameListener(ShadowSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (plugin.getDuelManager().isInDuel(player)) {
            plugin.getDuelManager().endDuel(player);
        }
    }
}
