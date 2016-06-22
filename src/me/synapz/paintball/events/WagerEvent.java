package me.synapz.paintball.events;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.PaintballPlayer;

/**
 * Created by Jeremy Lugo on 6/21/2016.
 */
public class WagerEvent extends ArenaEvent {

    private double amount;
    private WagerResult result;

    public WagerEvent(PaintballPlayer paintballPlayer, Arena arena, double amount, WagerResult result) {
        super(paintballPlayer, arena);
        this.amount = amount;
        this.result = result;
    }

    public double getAmount() {
        return amount;
    }

    public WagerResult getResult() {
        return result;
    }

    public enum WagerResult {
        SUCCESS, FAILURE;
    }
}
