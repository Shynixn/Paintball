package me.synapz.paintball.wager;

/**
 * Created by Jeremy Lugo on 6/21/2016.
 */
public class WagerManager {

    private int wagerAmount = 0;

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

    public int getWager() {
        return wagerAmount;
    }
}
