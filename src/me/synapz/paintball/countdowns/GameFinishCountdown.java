package me.synapz.paintball.countdowns;

import me.synapz.paintball.Arena;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.Bukkit;

public class GameFinishCountdown extends PaintballCountdown {

    public GameFinishCountdown(int counter, Arena arena) {
        super(arena, counter);

        arena.setState(Arena.ArenaState.STOPPING);
        tasks.put(arena, this);
    }

    public void onFinish() {
        arena.stopGame();
        tasks.remove(arena, this);

        for (ArenaPlayer player : arena.getAllArenaPlayers()) {
            player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
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
