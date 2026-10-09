package com.shadowsmp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageManager {
    private final Map<UUID, UUID> lastConversation = new HashMap<>();

    public void setReplyTarget(UUID sender, UUID target) {
        lastConversation.put(sender, target);
    }

    public UUID getReplyTarget(UUID sender) {
        return lastConversation.get(sender);
    }

    public void clearReplyTarget(UUID sender) {
        lastConversation.remove(sender);
    }
}
