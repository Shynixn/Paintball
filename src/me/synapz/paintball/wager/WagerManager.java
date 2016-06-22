package me.synapz.paintball.wager;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.arenas.Arena;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeremy Lugo on 6/21/2016.
 */
public class WagerManager {

    private Paintball plugin;
    private Map<Arena, Double> wagerAmounts;

    public WagerManager(Paintball plugin) {
        this.plugin = plugin;
        this.wagerAmounts = new HashMap<>();
    }

    public void addWager(Arena arena, double amount) {
        double currentAmount = wagerAmounts.getOrDefault(arena, 0.0);
        currentAmount += amount;
        wagerAmounts.put(arena, currentAmount);
    }

    public boolean hasWager(Arena arena) {
        return wagerAmounts.containsKey(arena)
                && wagerAmounts.get(arena) > 0.0;
    }

    public double getAndResetWager(Arena arena) {
        return wagerAmounts.remove(arena);
    }
}
