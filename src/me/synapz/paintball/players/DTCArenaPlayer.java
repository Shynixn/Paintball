package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.DTCArena;
import me.synapz.paintball.enums.Team;
import org.bukkit.entity.Player;

public class DTCArenaPlayer extends ArenaPlayer {

    private DTCArena dtcArena = (DTCArena) arena;

    public DTCArenaPlayer(LobbyPlayer lobbyPlayer) {
        super(lobbyPlayer);
    }

    @Override
    public void kill(ArenaPlayer arenaPlayer, String action) {
        arena.decrementTeamScore(team);
        super.kill(arenaPlayer, action);
    }

    @Override
    public void leave() {
        super.leave();

        // TODO: Increment Points Secured
    }
}
