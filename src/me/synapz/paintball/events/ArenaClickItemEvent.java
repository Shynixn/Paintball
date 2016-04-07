package me.synapz.paintball.events;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.coin.CoinItem;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;

public class ArenaClickItemEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private ArenaPlayer arenaPlayer;
    private Arena arena;
    private CoinItem coinItem;
    private Action action;

    public ArenaClickItemEvent(ArenaPlayer arenaPlayer, CoinItem coinItem, Action action) {
        this.arenaPlayer = arenaPlayer;
        this.coinItem = coinItem;
        this.arena = arenaPlayer.getArena();
        this.action = action;
    }

    public ArenaPlayer getArenaPlayer() {
        return this.arenaPlayer;
    }

    public CoinItem getCoinItem() {
        return this.coinItem;
    }

    public Arena getArena() {
        return this.arena;
    }

    public Action getAction() {
        return action;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}