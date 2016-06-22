package me.synapz.paintball.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Jeremy Lugo on 6/21/2016.
 */
public class PaintballEvent extends Event{

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
