package me.synapz.paintball.countdowns;

import me.synapz.paintball.Arena;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.players.ScoreboardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameFinishCountdown extends PaintballCountdown {

    public static Map<Arena, GameFinishCountdown> arenasFinishing = new HashMap<>();

    private Arena arena;

    public GameFinishCountdown(int counter, Arena arena) {
        super(counter);
        this.arena = arena;
        arena.setState(Arena.ArenaState.STOPPING);
        arenasFinishing.put(arena, this);
    }

    public void onFinish() {
        arena.stopGame();
        arenasFinishing.remove(arena, this);

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
        arenasFinishing.remove(arena, this);
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
