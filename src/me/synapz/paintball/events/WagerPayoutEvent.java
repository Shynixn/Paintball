package me.synapz.paintball.events;

import me.synapz.paintball.players.PaintballPlayer;

import java.util.List;

/**
 * Created by Jeremy Lugo on 6/22/2016.
 */
public class WagerPayoutEvent extends PaintballEvent{

    private List<PaintballPlayer> paintballPlayers;
    private double amount;

    public WagerPayoutEvent(List<PaintballPlayer> paintballPlayers, double amount) {
        this.paintballPlayers = paintballPlayers;
        this.amount = amount;
    }

    public List<PaintballPlayer> getPaintballPlayers() {
        return paintballPlayers;
    }

    public double getAmount() {
        return amount;
    }
}
