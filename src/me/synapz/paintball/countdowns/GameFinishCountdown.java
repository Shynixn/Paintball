package me.synapz.paintball.countdowns;

import me.synapz.paintball.Arena;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.Bukkit;

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
        arena.setState(Arena.ArenaState.WAITING);
        arena.forceLeaveArena();
        arenasFinishing.remove(arena, this);

        for (ArenaPlayer player : arena.getAllArenaPlayers()) {
            // TODO: put back old stored scoreboard
            player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    public void onIteration() {
        for (ArenaPlayer player : arena.getAllArenaPlayers()) {
            player.updateDisplayName();
        }
    }

    // if the arena is not in progress then just stop the counter
    public boolean stop() {
        return arena == null || arena.getState() != Arena.ArenaState.STOPPING;
    }

    public boolean intervalCheck() {
        return true;
    }
}
