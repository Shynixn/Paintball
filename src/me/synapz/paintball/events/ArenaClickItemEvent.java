package me.synapz.paintball.events;

import me.synapz.paintball.coin.CoinItem;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.event.block.Action;

public class ArenaClickItemEvent extends ArenaBuyItemEvent {

    private Action action;

    public ArenaClickItemEvent(ArenaPlayer arenaPlayer, CoinItem coinItem, Action action) {
        super(arenaPlayer, coinItem);

        this.action = action;
    }

    public Action getAction() {
        return action;
    }


}