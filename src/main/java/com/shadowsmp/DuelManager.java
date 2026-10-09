package com.shadowsmp;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelManager {
    private final ShadowSMP plugin;
    private final Map<UUID, UUID> duelRequests = new HashMap<>();
    private final Map<Player, Player> activeDuels = new HashMap<>();

    public DuelManager(ShadowSMP plugin) {
        this.plugin = plugin;
    }

    public void sendDuelRequest(Player challenger, Player opponent) {
        if (activeDuels.containsKey(challenger)) {
            plugin.sendMessage(challenger, "&cYou are already in a duel.");
            return;
        }
        if (activeDuels.containsKey(opponent)) {
            plugin.sendMessage(challenger, "&cThat player is already in a duel.");
            return;
        }
        duelRequests.put(opponent.getUniqueId(), challenger.getUniqueId());
        plugin.sendMessage(challenger, "&aDuel request sent to " + opponent.getName());
        plugin.sendMessage(opponent, "&7" + challenger.getName() + " &ahas challenged you to a duel. Use /duelaccept");
    }

    public void acceptDuel(Player opponent) {
        UUID challengerUUID = duelRequests.get(opponent.getUniqueId());
        if (challengerUUID == null) {
            plugin.sendMessage(opponent, "&cYou have no pending duel requests.");
            return;
        }
        Player challenger = org.bukkit.Bukkit.getPlayer(challengerUUID);
        if (challenger == null) {
            plugin.sendMessage(opponent, "&cThe challenger is no longer online.");
            duelRequests.remove(opponent.getUniqueId());
            return;
        }
        duelRequests.remove(opponent.getUniqueId());
        activeDuels.put(challenger, opponent);
        activeDuels.put(opponent, challenger);
        plugin.sendMessage(challenger, "&aDuel accepted! Let the battle begin!");
        plugin.sendMessage(opponent, "&aDuel accepted! Let the battle begin!");
    }

    public void endDuel(Player player) {
        Player opponent = activeDuels.remove(player);
        if (opponent != null) {
            activeDuels.remove(opponent);
            plugin.sendMessage(player, "&aDuel ended.");
            plugin.sendMessage(opponent, "&aDuel ended.");
        }
    }

    public boolean isInDuel(Player player) {
        return activeDuels.containsKey(player);
    }
}
