package com.shadowsmp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShadowSMP extends JavaPlugin {
    private PlayerDataManager playerDataManager;
    private SpawnManager spawnManager;
    private TeleportManager teleportManager;
    private MessageManager messageManager;
    private GameManager gameManager;
    private BedWarsManager bedWarsManager;
    private SkyWarsManager skyWarsManager;
    private DuelManager duelManager;
    private GunManager gunManager;
    private VehicleManager vehicleManager;

    private boolean luckPermsEnabled;
    private boolean vaultEnabled;
    private boolean placeholderApiEnabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.playerDataManager = new PlayerDataManager(this);
        this.spawnManager = new SpawnManager(this);
        this.teleportManager = new TeleportManager();
        this.messageManager = new MessageManager();
        this.gameManager = new GameManager(this);
        this.bedWarsManager = new BedWarsManager(this);
        this.skyWarsManager = new SkyWarsManager(this);
        this.duelManager = new DuelManager(this);
        this.gunManager = new GunManager(this);
        this.vehicleManager = new VehicleManager(this);

        this.playerDataManager.load();
        this.spawnManager.loadSpawnFromConfig();

        this.luckPermsEnabled = Bukkit.getPluginManager().isPluginEnabled("LuckPerms");
        this.vaultEnabled = Bukkit.getPluginManager().isPluginEnabled("Vault");
        this.placeholderApiEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        registerCommands();

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new PlayerListener(this), this);
        manager.registerEvents(new GameListener(this), this);
        manager.registerEvents(new GunListener(this), this);

        getLogger().info("ShadowSMP enabled successfully.");
        getLogger().info("Optional integrations: LuckPerms=" + luckPermsEnabled + ", Vault=" + vaultEnabled + ", PlaceholderAPI=" + placeholderApiEnabled);
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.save();
        }
        if (bedWarsManager != null) {
            bedWarsManager.stopAllGames();
        }
        if (skyWarsManager != null) {
            skyWarsManager.stopAllGames();
        }
    }

    private void registerCommands() {
        if (getCommand("shadow") != null) {
            getCommand("shadow").setExecutor(new CommandExecutors.ShadowCommandExecutor(this));
        }
        if (getCommand("spawn") != null) {
            getCommand("spawn").setExecutor(new CommandExecutors.SpawnCommandExecutor(this));
        }
        if (getCommand("setspawn") != null) {
            getCommand("setspawn").setExecutor(new CommandExecutors.SpawnCommandExecutor(this));
        }
        if (getCommand("home") != null) {
            getCommand("home").setExecutor(new CommandExecutors.HomeCommandExecutor(this));
        }
        if (getCommand("sethome") != null) {
            getCommand("sethome").setExecutor(new CommandExecutors.HomeCommandExecutor(this));
        }
        if (getCommand("tpa") != null) {
            getCommand("tpa").setExecutor(new CommandExecutors.TeleportCommandExecutor(this));
        }
        if (getCommand("tpaccept") != null) {
            getCommand("tpaccept").setExecutor(new CommandExecutors.TeleportCommandExecutor(this));
        }
        if (getCommand("msg") != null) {
            getCommand("msg").setExecutor(new CommandExecutors.MessageCommandExecutor(this));
        }
        if (getCommand("r") != null) {
            getCommand("r").setExecutor(new CommandExecutors.MessageCommandExecutor(this));
        }
        if (getCommand("fly") != null) {
            getCommand("fly").setExecutor(new CommandExecutors.StaffCommandExecutor(this));
        }
        if (getCommand("vanish") != null) {
            getCommand("vanish").setExecutor(new CommandExecutors.StaffCommandExecutor(this));
        }
        if (getCommand("heal") != null) {
            getCommand("heal").setExecutor(new CommandExecutors.StaffCommandExecutor(this));
        }
        if (getCommand("feed") != null) {
            getCommand("feed").setExecutor(new CommandExecutors.StaffCommandExecutor(this));
        }
        if (getCommand("invsee") != null) {
            getCommand("invsee").setExecutor(new CommandExecutors.StaffCommandExecutor(this));
        }
        if (getCommand("staffchat") != null) {
            getCommand("staffchat").setExecutor(new CommandExecutors.StaffCommandExecutor(this));
        }
        if (getCommand("tokens") != null) {
            getCommand("tokens").setExecutor(new CommandExecutors.TokenCommandExecutor(this));
        }
        if (getCommand("gamemenu") != null) {
            getCommand("gamemenu").setExecutor(new CommandExecutors.GameMenuCommandExecutor(this));
        }
        if (getCommand("bedwars") != null) {
            getCommand("bedwars").setExecutor(new CommandExecutors.BedWarsCommandExecutor(this));
        }
        if (getCommand("skywars") != null) {
            getCommand("skywars").setExecutor(new CommandExecutors.SkyWarsCommandExecutor(this));
        }
        if (getCommand("duel") != null) {
            getCommand("duel").setExecutor(new CommandExecutors.DuelCommandExecutor(this));
        }
        if (getCommand("duelaccept") != null) {
            getCommand("duelaccept").setExecutor(new CommandExecutors.DuelCommandExecutor(this));
        }
        if (getCommand("gun") != null) {
            getCommand("gun").setExecutor(new CommandExecutors.GunCommandExecutor(this));
        }
        if (getCommand("vehicle") != null) {
            getCommand("vehicle").setExecutor(new CommandExecutors.VehicleCommandExecutor(this));
        }
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public BedWarsManager getBedWarsManager() {
        return bedWarsManager;
    }

    public SkyWarsManager getSkyWarsManager() {
        return skyWarsManager;
    }

    public DuelManager getDuelManager() {
        return duelManager;
    }

    public GunManager getGunManager() {
        return gunManager;
    }

    public VehicleManager getVehicleManager() {
        return vehicleManager;
    }

    public boolean isLuckPermsEnabled() {
        return luckPermsEnabled;
    }

    public boolean isVaultEnabled() {
        return vaultEnabled;
    }

    public boolean isPlaceholderApiEnabled() {
        return placeholderApiEnabled;
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.prefix", "&8[&bShadowSMP&8]&r "));
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(getPrefix() + ChatColor.translateAlternateColorCodes('&', message));
    }
}
