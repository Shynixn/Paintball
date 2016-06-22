package me.synapz.paintball.events;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.coin.CoinItem;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaBuyItemEvent extends ArenaEvent {

    private CoinItem coinItem;
    private ArenaPlayer arenaPlayer;

    public ArenaBuyItemEvent(ArenaPlayer arenaPlayer, CoinItem coinItem) {
        super(arenaPlayer, arenaPlayer.getArena());
        this.coinItem = coinItem;
        this.arenaPlayer = arenaPlayer;
    }

    public CoinItem getCoinItem() {
        return this.coinItem;
    }

    public ArenaPlayer getArenaPlayer() {
        return arenaPlayer;
    }
}
