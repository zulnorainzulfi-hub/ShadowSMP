package com.shadowsmp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

public final class CommandExecutors {
    private CommandExecutors() {
    }

    public static final class ShadowCommandExecutor implements CommandExecutor {
        private final ShadowSMP plugin;

        public ShadowCommandExecutor(ShadowSMP plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!sender.hasPermission("shadowsmp.command.shadow")) {
                plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                return true;
            }

            if (args.length == 0) {
                plugin.sendMessage(sender, "&bShadowSMP &7v1.0.0");
                plugin.sendMessage(sender, "&7Commands: /shadow info, /shadow reload, /spawn, /home, /tpa, /msg, /tokens");
                return true;
            }

            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "info" -> {
                    plugin.sendMessage(sender, "&bShadowSMP &7- All-in-one SMP plugin");
                    plugin.sendMessage(sender, "&7Features: spawn, homes, tpa, messaging, staff tools, tokens");
                    plugin.sendMessage(sender, "&7Optional integrations: LuckPerms, Vault, PlaceholderAPI");
                    return true;
                }
                case "reload" -> {
                    if (!sender.hasPermission("shadowsmp.command.shadow")) {
                        plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                        return true;
                    }
                    plugin.reloadConfig();
                    plugin.getPlayerDataManager().load();
                    plugin.getSpawnManager().loadSpawnFromConfig();
                    plugin.sendMessage(sender, "&aShadowSMP configuration reloaded.");
                    return true;
                }
                default -> {
                    plugin.sendMessage(sender, "&cUnknown subcommand. Try: /shadow info or /shadow reload");
                    return true;
                }
            }
        }
    }

    public static final class SpawnCommandExecutor implements CommandExecutor {
        private final ShadowSMP plugin;

        public SpawnCommandExecutor(ShadowSMP plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                return true;
            }

            String commandName = command.getName().toLowerCase(Locale.ROOT);
            if ("spawn".equals(commandName)) {
                if (!sender.hasPermission("shadowsmp.command.spawn")) {
                    plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                    return true;
                }
                plugin.getSpawnManager().teleportToSpawn(player);
                return true;
            }

            if ("setspawn".equals(commandName)) {
                if (!sender.hasPermission("shadowsmp.command.setspawn")) {
                    plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                    return true;
                }
                plugin.getSpawnManager().setSpawn(player.getLocation());
                plugin.sendMessage(player, "&aSpawn point set successfully.");
                return true;
            }

            return false;
        }
    }

    public static final class HomeCommandExecutor implements CommandExecutor {
        private final ShadowSMP plugin;

        public HomeCommandExecutor(ShadowSMP plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                return true;
            }

            String cmdName = command.getName().toLowerCase(Locale.ROOT);
            String homeName = (args.length > 0 && !args[0].isBlank()) ? args[0] : "default";

            if ("sethome".equals(cmdName)) {
                if (!sender.hasPermission("shadowsmp.command.sethome")) {
                    plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                    return true;
                }
                plugin.getPlayerDataManager().setHome(player.getUniqueId(), homeName, player.getLocation());
                plugin.sendMessage(player, "&aHome '&7" + homeName + "&a' set successfully.");
                return true;
            }

            if ("home".equals(cmdName)) {
                if (!sender.hasPermission("shadowsmp.command.home")) {
                    plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                    return true;
                }
                Location home = plugin.getPlayerDataManager().getHome(player.getUniqueId(), homeName);
                if (home == null) {
                    plugin.sendMessage(player, "&cYou do not have a home named '&7" + homeName + "&c'.");
                    return true;
                }
                player.teleport(home);
                plugin.sendMessage(player, "&aTeleported to home '&7" + homeName + "&a'.");
                return true;
            }

            return false;
        }
    }

    public static final class TeleportCommandExecutor implements CommandExecutor {
        private final ShadowSMP plugin;

        public TeleportCommandExecutor(ShadowSMP plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                return true;
            }

            switch (command.getName().toLowerCase(Locale.ROOT)) {
                case "tpa" -> {
                    if (!sender.hasPermission("shadowsmp.command.tpa")) {
                        plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                        return true;
                    }
                    if (args.length != 1) {
                        plugin.sendMessage(player, "&cUsage: /tpa <player>");
                        return true;
                    }
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (target == null || !target.isOnline()) {
                        plugin.sendMessage(player, plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found."));
                        return true;
                    }
                    if (target.equals(player)) {
                        plugin.sendMessage(player, "&cYou cannot teleport to yourself.");
                        return true;
                    }
                    plugin.getTeleportManager().createRequest(player, target);
                    plugin.sendMessage(player, "&aTeleport request sent to &7" + target.getName() + "&a.");
                    plugin.sendMessage(target, "&7" + player.getName() + " &ahas sent you a teleport request. Use /tpaccept to accept.");
                    return true;
                }
                case "tpaccept" -> {
                    if (!sender.hasPermission("shadowsmp.command.tpaccept")) {
                        plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                        return true;
                    }
                    Player requester = plugin.getTeleportManager().getRequester(player);
                    if (requester == null) {
                        plugin.sendMessage(player, "&cYou do not have any pending teleport requests.");
                        return true;
                    }
                    plugin.getTeleportManager().removeRequest(player.getUniqueId());
                    requester.teleport(player.getLocation());
                    plugin.sendMessage(requester, "&aTeleport accepted.");
                    plugin.sendMessage(player, "&aTeleport request accepted.");
                    return true;
                }
                default -> {
                    return false;
                }
            }
        }
    }

    public static final class MessageCommandExecutor implements CommandExecutor {
        private final ShadowSMP plugin;

        public MessageCommandExecutor(ShadowSMP plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                return true;
            }

            if ("msg".equalsIgnoreCase(command.getName())) {
                if (!sender.hasPermission("shadowsmp.command.msg")) {
                    plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                    return true;
                }
                if (args.length < 2) {
                    plugin.sendMessage(player, "&cUsage: /msg <player> <message>");
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    plugin.sendMessage(player, plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found."));
                    return true;
                }

                String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                plugin.getMessageManager().setReplyTarget(player.getUniqueId(), target.getUniqueId());
                plugin.getMessageManager().setReplyTarget(target.getUniqueId(), player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "You -> " + target.getName() + ": " + message);
                target.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + " -> You: " + message);
                return true;
            }

            if ("r".equalsIgnoreCase(command.getName())) {
                if (!sender.hasPermission("shadowsmp.command.r")) {
                    plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                    return true;
                }
                if (args.length < 1) {
                    plugin.sendMessage(player, "&cUsage: /r <message>");
                    return true;
                }

                UUID targetUUID = plugin.getMessageManager().getReplyTarget(player.getUniqueId());
                if (targetUUID == null) {
                    plugin.sendMessage(player, "&cYou have nobody to reply to.");
                    return true;
                }

                Player target = Bukkit.getPlayer(targetUUID);
                if (target == null) {
                    plugin.sendMessage(player, "&cThe player is no longer online.");
                    return true;
                }

                String message = String.join(" ", args);
                player.sendMessage(ChatColor.GREEN + "You -> " + target.getName() + ": " + message);
                target.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + " -> You: " + message);
                return true;
            }

            return false;
        }
    }

    public static final class StaffCommandExecutor implements CommandExecutor {
        private final ShadowSMP plugin;

        public StaffCommandExecutor(ShadowSMP plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                return true;
            }

            String name = command.getName().toLowerCase(Locale.ROOT);

            switch (name) {
                case "fly" -> {
                    if (!sender.hasPermission("shadowsmp.command.fly")) {
                        plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                        return true;
                    }
                    Player target = player;
                    if (args.length > 0) {
                        if (!sender.hasPermission("shadowsmp.staff.others")) {
                            plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                            return true;
                        }
                        target = Bukkit.getPlayerExact(args[0]);
                        if (target == null) {
                            plugin.sendMessage(player, plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found."));
                            return true;
                        }
                    }
                    target.setAllowFlight(!target.getAllowFlight());
                    target.setFlying(target.getAllowFlight());
                    plugin.sendMessage(player, "&aSet fly mode for &7" + target.getName() + " &ato " + target.getAllowFlight() + ".");
                    return true;
                }
                case "vanish" -> {
                    if (!sender.hasPermission("shadowsmp.command.vanish")) {
                        plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                        return true;
                    }
                    Player target = player;
                    if (args.length > 0) {
                        if (!sender.hasPermission("shadowsmp.staff.others")) {
                            plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                            return true;
                        }
                        target = Bukkit.getPlayerExact(args[0]);
                        if (target == null) {
                            plugin.sendMessage(player, plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found."));
                            return true;
                        }
                    }
                    boolean vanished = target.hasMetadata("vanished");
                    if (vanished) {
                        target.removeMetadata("vanished", plugin);
                        for (Player viewer : Bukkit.getOnlinePlayers()) {
                            viewer.showPlayer(plugin, target);
                        }
                        plugin.sendMessage(target, "&aVanish disabled.");
                    } else {
                        target.setMetadata("vanished", new FixedMetadataValue(plugin, true));
                        for (Player viewer : Bukkit.getOnlinePlayers()) {
                            if (viewer != target) {
                                viewer.hidePlayer(plugin, target);
                            }
                        }
                        plugin.sendMessage(target, "&aVanish enabled.");
                    }
                    return true;
                }
                case "heal" -> {
                    if (!sender.hasPermission("shadowsmp.command.heal")) {
                        plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                        return true;
                    }
                    Player target = player;
                    if (args.length > 0) {
                        if (!sender.hasPermission("shadowsmp.staff.others")) {
                            plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                            return true;
                        }
                        target = Bukkit.getPlayerExact(args[0]);
                        if (target == null) {
                            plugin.sendMessage(player, plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found."));
                            return true;
                        }
                    }
                    target.setHealth(target.getMaxHealth());
                    target.setFoodLevel(20);
                    target.setSaturation(20.0F);
                    target.setFireTicks(0);
                    plugin.sendMessage(target, "&aYou have been healed.");
                    return true;
                }
                case "feed" -> {
                    if (!sender.hasPermission("shadowsmp.command.feed")) {
                        plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                        return true;
                    }
                    Player target = player;
                    if (args.length > 0) {
                        if (!sender.hasPermission("shadowsmp.staff.others")) {
                            plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                            return true;
                        }
                        target = Bukkit.getPlayerExact(args[0]);
                        if (target == null) {
                            plugin.sendMessage(player, plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found."));
                            return true;
                        }
                    }
                    target.setFoodLevel(20);
                    target.setSaturation(20.0F);
                    plugin.sendMessage(target, "&aYou have been fed.");
                    return true;
                }
                case "invsee" -> {
                    if (!sender.hasPermission("shadowsmp.command.invsee")) {
                        plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                        return true;
                    }
                    if (args.length != 1) {
                        plugin.sendMessage(player, "&cUsage: /invsee <player>");
                        return true;
                    }
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (target == null) {
                        plugin.sendMessage(player, plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found."));
                        return true;
                    }
                    Inventory inventory = target.getInventory();
                    player.openInventory(inventory);
                    plugin.sendMessage(player, "&aViewing inventory of &7" + target.getName());
                    return true;
                }
                case "staffchat" -> {
                    if (!sender.hasPermission("shadowsmp.command.staffchat")) {
                        plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                        return true;
                    }
                    if (args.length < 1) {
                        plugin.sendMessage(player, "&cUsage: /staffchat <message>");
                        return true;
                    }
                    String message = String.join(" ", args);
                    String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("staff.chat-prefix", "&8[&6StaffChat&8]&r"));
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (online.hasPermission("shadowsmp.staffchat.receive") || online.hasPermission("shadowsmp.*")) {
                            online.sendMessage(prefix + " " + ChatColor.AQUA + player.getName() + ChatColor.GRAY + ": " + message);
                        }
                    }
                    return true;
                }
                default -> {
                    return false;
                }
            }
        }
    }

    public static final class TokenCommandExecutor implements CommandExecutor {
        private final ShadowSMP plugin;

        public TokenCommandExecutor(ShadowSMP plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                return true;
            }

            if (!player.hasPermission("shadowsmp.command.tokens")) {
                plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                return true;
            }

            if (args.length == 0) {
                int tokens = plugin.getPlayerDataManager().getTokens(player.getUniqueId());
                plugin.sendMessage(player, "&aYou currently have &7" + tokens + " &atokens.");
                return true;
            }

            if (args.length == 3 && args[0].equalsIgnoreCase("pay")) {
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    plugin.sendMessage(player, plugin.getConfig().getString("messages.invalid-number", "&cInvalid number."));
                    return true;
                }
                if (amount <= 0) {
                    plugin.sendMessage(player, "&cAmount must be greater than zero.");
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    plugin.sendMessage(player, plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found."));
                    return true;
                }
                if (target.equals(player)) {
                    plugin.sendMessage(player, "&cYou cannot pay yourself.");
                    return true;
                }

                int senderTokens = plugin.getPlayerDataManager().getTokens(player.getUniqueId());
                if (senderTokens < amount) {
                    plugin.sendMessage(player, "&cYou do not have enough tokens.");
                    return true;
                }

                plugin.getPlayerDataManager().removeTokens(player.getUniqueId(), amount);
                plugin.getPlayerDataManager().addTokens(target.getUniqueId(), amount);
                plugin.sendMessage(player, "&aYou paid &7" + target.getName() + " &a" + amount + " tokens.");
                plugin.sendMessage(target, "&aYou received &7" + amount + " &atokens from &7" + player.getName() + "&a.");
                return true;
            }

            plugin.sendMessage(player, "&cUsage: /tokens or /tokens pay <player> <amount>");
            return true;
        }
    }

    public static final class GameMenuCommandExecutor implements CommandExecutor {
        private final ShadowSMP plugin;

        public GameMenuCommandExecutor(ShadowSMP plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                return true;
            }

            if (!sender.hasPermission("shadowsmp.command.gamemenu")) {
                plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                return true;
            }

            plugin.getGameManager().openGameMenu(player);
            return true;
        }
    }

    public static final class BedWarsCommandExecutor implements CommandExecutor {
        private final ShadowSMP plugin;

        public BedWarsCommandExecutor(ShadowSMP plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                return true;
            }

            if (!sender.hasPermission("shadowsmp.command.bedwars")) {
                plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                return true;
            }

            if (args.length == 0) {
                plugin.sendMessage(player, "&cUsage: /bedwars [join|leave|list]");
                return true;
            }

            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "join" -> {
                    plugin.getBedWarsManager().joinGame(player);
                    return true;
                }
                case "leave" -> {
                    plugin.getBedWarsManager().leaveGame(player);
                    return true;
                }
                case "list" -> {
                    plugin.getBedWarsManager().listGames();
                    return true;
                }
                default -> {
                    plugin.sendMessage(player, "&cUsage: /bedwars [join|leave|list]");
                    return true;
                }
            }
        }
    }

    public static final class SkyWarsCommandExecutor implements CommandExecutor {
        private final ShadowSMP plugin;

        public SkyWarsCommandExecutor(ShadowSMP plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                return true;
            }

            if (!sender.hasPermission("shadowsmp.command.skywars")) {
                plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                return true;
            }

            if (args.length == 0) {
                plugin.sendMessage(player, "&cUsage: /skywars [join|leave|list]");
                return true;
            }

            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "join" -> {
                    plugin.getSkyWarsManager().joinGame(player);
                    return true;
                }
                case "leave" -> {
                    plugin.getSkyWarsManager().leaveGame(player);
                    return true;
                }
                case "list" -> {
                    plugin.getSkyWarsManager().listGames();
                    return true;
                }
                default -> {
                    plugin.sendMessage(player, "&cUsage: /skywars [join|leave|list]");
                    return true;
                }
            }
        }
    }

    public static final class DuelCommandExecutor implements CommandExecutor {
        private final ShadowSMP plugin;

        public DuelCommandExecutor(ShadowSMP plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                return true;
            }

            if (!sender.hasPermission("shadowsmp.command.duel")) {
                plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                return true;
            }

            String cmdName = command.getName().toLowerCase(Locale.ROOT);
            if ("duel".equals(cmdName)) {
                if (args.length != 1) {
                    plugin.sendMessage(player, "&cUsage: /duel <player>");
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    plugin.sendMessage(player, plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found."));
                    return true;
                }

                plugin.getDuelManager().sendDuelRequest(player, target);
                return true;
            }

            if ("duelaccept".equals(cmdName)) {
                if (!sender.hasPermission("shadowsmp.command.duelaccept")) {
                    plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                    return true;
                }
                plugin.getDuelManager().acceptDuel(player);
                return true;
            }

            return false;
        }
    }

    public static final class GunCommandExecutor implements CommandExecutor {
        private final ShadowSMP plugin;

        public GunCommandExecutor(ShadowSMP plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                return true;
            }

            if (!sender.hasPermission("shadowsmp.command.gun")) {
                plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                return true;
            }

            if (args.length == 0) {
                plugin.sendMessage(player, "&cUsage: /gun [give|reload]");
                return true;
            }

            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "give" -> {
                    plugin.getGunManager().giveGun(player);
                    return true;
                }
                case "reload" -> {
                    plugin.getGunManager().reloadGun(player);
                    return true;
                }
                default -> {
                    plugin.sendMessage(player, "&cUsage: /gun [give|reload]");
                    return true;
                }
            }
        }
    }

    public static final class VehicleCommandExecutor implements CommandExecutor {
        private final ShadowSMP plugin;

        public VehicleCommandExecutor(ShadowSMP plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                return true;
            }

            if (!sender.hasPermission("shadowsmp.command.vehicle")) {
                plugin.sendMessage(sender, plugin.getConfig().getString("messages.no-permission", "&cYou do not have permission to use that command."));
                return true;
            }

            if (args.length == 0) {
                plugin.sendMessage(player, "&cUsage: /vehicle [spawn <type>|list]");
                return true;
            }

            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "spawn" -> {
                    if (args.length < 2) {
                        plugin.sendMessage(player, "&cUsage: /vehicle spawn <car|bike|helicopter>");
                        return true;
                    }
                    plugin.getVehicleManager().spawnVehicle(player, args[1]);
                    return true;
                }
                case "list" -> {
                    plugin.getVehicleManager().listVehicles();
                    return true;
                }
                default -> {
                    plugin.sendMessage(player, "&cUsage: /vehicle [spawn <type>|list]");
                    return true;
                }
            }
        }
    }
}
