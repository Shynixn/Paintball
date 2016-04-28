package me.synapz.paintball.events;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.coin.CoinItem;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaBuyItemEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private ArenaPlayer arenaPlayer;
    private Arena arena;
    private CoinItem coinItem;

    public ArenaBuyItemEvent(ArenaPlayer arenaPlayer, CoinItem coinItem) {
        this.arenaPlayer = arenaPlayer;
        this.coinItem = coinItem;
        this.arena = arenaPlayer.getArena();
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

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
