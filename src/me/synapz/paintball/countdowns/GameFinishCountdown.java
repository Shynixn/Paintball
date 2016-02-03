package me.synapz.paintball.countdowns;

import me.synapz.paintball.Arena;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class GameFinishCountdown extends PaintballCountdown {

    public static List<Arena> arenasFinishing = new ArrayList<>();

    private Arena arena;

    public GameFinishCountdown(int counter, Arena arena) {
        super(counter);
        this.arena = arena;
        arenasFinishing.add(arena);
    }

    public void onFinish() {
        arena.setState(Arena.ArenaState.WAITING);
        arena.forceLeaveArena();
        arenasFinishing.remove(arena);

        for (ArenaPlayer player : arena.getAllArenaPlayers()) {
            player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    public void onIteration() {

    }

    // if the arena is not in progress then just stop the counter
    public boolean stop() {
        return arena == null || arena.getState() != Arena.ArenaState.IN_PROGRESS;
    }

    public boolean intervalCheck() {
        return true;
    }
}
