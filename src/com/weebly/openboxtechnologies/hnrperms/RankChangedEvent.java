package com.weebly.openboxtechnologies.hnrperms;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class RankChangedEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private UUID id;

    public RankChangedEvent(UUID player) {
        id = player;
    }

    public UUID getPlayer() {
        return id;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
