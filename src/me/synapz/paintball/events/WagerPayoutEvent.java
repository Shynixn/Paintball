package me.synapz.paintball.events;

import me.synapz.paintball.players.ArenaPlayer;

import java.util.List;

/**
 * Created by Jeremy Lugo on 6/22/2016.
 */
public class WagerPayoutEvent extends PaintballEvent{

    private List<ArenaPlayer> arenaPlayers;
    private double amount;

    public WagerPayoutEvent(List<ArenaPlayer> arenaPlayers, double amount) {
        this.arenaPlayers = arenaPlayers;
        this.amount = amount;
    }

    public List<ArenaPlayer> getArenaPlayers() {
        return arenaPlayers;
    }

    public double getAmount() {
        return amount;
    }
}
