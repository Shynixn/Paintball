package me.synapz.paintball.countdowns;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.players.ArenaPlayer;

import java.util.List;

public class GameFinishCountdown extends PaintballCountdown {

    private final List<ArenaPlayer> winners;
    private final List<ArenaPlayer> losers;
    private final List<ArenaPlayer> tiers;

    public GameFinishCountdown(int counter, Arena arena, List<ArenaPlayer> winners, List<ArenaPlayer> losers, List<ArenaPlayer> tiers) {
        super(arena, counter);

        arena.setState(Arena.ArenaState.STOPPING);
        tasks.put(arena, this);

        this.winners = winners;
        this.losers = losers;
        this.tiers = tiers;
    }

    public void onFinish() {
        arena.stopGame();
        tasks.remove(arena, this);

        for (ArenaPlayer winner : winners) {
            arena.sendCommands(winner.getPlayer(), arena.WIN_COMMANDS);
        }

        for (ArenaPlayer loser : losers) {
            arena.sendCommands(loser.getPlayer(), arena.LOOSE_COMMANDS);
        }

        for (ArenaPlayer tier : tiers) {
            arena.sendCommands(tier.getPlayer(), arena.TIE_COMMANDS);
        }
    }

    public void onIteration() {
        arena.updateSigns();
        arena.updateAllScoreboardTimes();
    }

    @Override
    public void cancel() {
        super.cancel();
        tasks.remove(arena, this);
        arena.updateSigns();
    }

    // if the arena is not in progress then just stop the counter
    public boolean stop() {
        return arena == null || arena.getState() != Arena.ArenaState.STOPPING;
    }

    public boolean intervalCheck() {
        return true;
    }
}
