package com.shadowsmp;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GunManager {
    private final ShadowSMP plugin;
    private final Map<UUID, Long> gunCooldowns = new HashMap<>();
    private long reloadTime = 1000;
    private long cooldown = 500;

    public GunManager(ShadowSMP plugin) {
        this.plugin = plugin;
        this.reloadTime = plugin.getConfig().getLong("guns.reload-time", 1000);
        this.cooldown = plugin.getConfig().getLong("guns.cooldown", 500);
    }

    public void giveGun(Player player) {
        if (!plugin.getConfig().getBoolean("guns.enabled", true)) {
            plugin.sendMessage(player, "&cGuns are disabled.");
            return;
        }
        ItemStack gun = new ItemStack(Material.STICK);
        ItemMeta meta = gun.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("\u00a76Gun");
            gun.setItemMeta(meta);
        }
        player.getInventory().addItem(gun);
        plugin.sendMessage(player, "&aGun added to your inventory.");
    }

    public void reloadGun(Player player) {
        plugin.sendMessage(player, "&aGun reloaded.");
    }

    public boolean canShoot(Player player) {
        UUID uuid = player.getUniqueId();
        Long lastShot = gunCooldowns.get(uuid);
        if (lastShot == null) {
            return true;
        }
        return System.currentTimeMillis() - lastShot >= cooldown;
    }

    public void recordShot(Player player) {
        gunCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
