package com.shadowsmp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {
    private final Map<UUID, UUID> pendingRequests = new HashMap<>();

    public void createRequest(Player requester, Player target) {
        pendingRequests.put(target.getUniqueId(), requester.getUniqueId());
    }

    public UUID getRequesterUUID(UUID targetUUID) {
        return pendingRequests.get(targetUUID);
    }

    public void removeRequest(UUID targetUUID) {
        pendingRequests.remove(targetUUID);
    }

    public Player getRequester(Player target) {
        UUID requesterId = pendingRequests.get(target.getUniqueId());
        if (requesterId == null) {
            return null;
        }
        return Bukkit.getPlayer(requesterId);
    }
}
