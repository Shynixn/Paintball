package me.synapz.paintball.countdowns;

import me.synapz.paintball.Arena;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.players.ScoreboardPlayer;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.ChatColor.*;

public class LobbyCountdown extends PaintballCountdown {


    public static final Map<Arena, LobbyCountdown> tasks = new HashMap<>();
    private final Arena arena;

    public LobbyCountdown(int counter, Arena a) {
        super(counter); // adds 1 so to human eyes it goes from 5 to 1 instead of 4 to 0

        this.arena = a;
        tasks.put(arena, this);
    }

    public void onFinish() {
        arena.broadcastMessage(GREEN + "Teleporting into arena...");
        arena.startGame();
    }

    // Called every iteration of run()
    public void onIteration() {
        String prefix = GREEN + "Waiting ";
        String suffix = GRAY + "" + ((int) counter) + GREEN + " seconds!";

        arena.broadcastMessage(prefix + suffix);
        arena.broadcastTitle(prefix, suffix, 20, 30, 20);
    }

    public boolean stop() {
        return arena.getState() != Arena.ArenaState.WAITING || (arena.getLobbyPlayers().size() < arena.getMin());
    }

    public boolean intervalCheck() {
        arena.updateAllScoreboardTimes();
        arena.updateSigns();

        return counter <= arena.LOBBY_NO_INTERVAL || counter % arena.LOBBY_INTERVAL == 0;
    }

    @Override
    public void cancel() {
        super.cancel();
        tasks.remove(arena, this);
        arena.updateSigns();
    }
}
