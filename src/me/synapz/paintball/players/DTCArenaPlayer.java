package me.synapz.paintball.players;

import me.synapz.paintball.arenas.DTCArena;

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
