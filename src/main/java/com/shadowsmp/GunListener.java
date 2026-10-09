package com.shadowsmp;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;

public class GunListener implements Listener {
    private final ShadowSMP plugin;

    public GunListener(ShadowSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getItem() != null && event.getItem().getType() == Material.STICK) {
            if (event.getItem().getItemMeta() != null && event.getItem().getItemMeta().getDisplayName().contains("Gun")) {
                if (plugin.getGunManager().canShoot(player)) {
                    plugin.getGunManager().recordShot(player);
                    plugin.sendMessage(player, "&a*POP*");
                    event.setCancelled(true);
                }
            }
        }
    }
}
