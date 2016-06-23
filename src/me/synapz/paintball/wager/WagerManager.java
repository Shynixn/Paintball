package me.synapz.paintball.wager;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.arenas.Arena;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeremy Lugo on 6/21/2016.
 */
public class WagerManager {

    private double wagerAmount = 0d;

    public void addWager(double amount) {
        wagerAmount += amount;
    }

    public boolean hasWager() {
        return wagerAmount > 0.0;
    }

    public double getAndResetWager() {
        double savedWager = wagerAmount;
        wagerAmount = 0;
        return savedWager;
    }

    public double getWager() {
        return wagerAmount;
    }
}
