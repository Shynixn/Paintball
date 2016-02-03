package me.synapz.paintball.countdowns;

import me.synapz.paintball.Arena;
import me.synapz.paintball.players.LobbyPlayer;

import java.util.HashMap;
import java.util.Map;

public class LobbyCountdown extends PaintballCountdown {

    private static Map<Arena, LobbyCountdown> lobbyCountdowns = new HashMap<>();

    private Arena arena;

    public LobbyCountdown(int counter, Arena arena) {
        super(counter);
        this.arena = arena;

        if (lobbyCountdowns.get(arena) == null) {
            lobbyCountdowns.put(arena, this);
        } else {
            this.cancel();
        }
    }

    public void onFinish() {
        lobbyCountdowns.remove(arena, this);
        if (arena != null && arena.getState() == Arena.ArenaState.WAITING) {
            new LobbyCountdown(1, arena);
        }
    }

    public void onIteration() {
        for (LobbyPlayer player : arena.getLobbyPlayers()) {
            player.updateScoreboard();
        }
    }

    // When there is no more Lobby, or players it will stop // TODO if this is null will it throw error?
    public boolean stop() {
        return arena == null || arena.getState() != Arena.ArenaState.WAITING || arena.getLobbyPlayers().isEmpty();
    }

    public boolean intervalCheck() {
        return true;
    }
}
