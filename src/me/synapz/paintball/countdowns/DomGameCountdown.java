package me.synapz.paintball.countdowns;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.DomArena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.players.DomArenaPlayer;
import me.synapz.paintball.players.PaintballPlayer;

public class DomGameCountdown extends GameCountdown {

    public DomGameCountdown(Arena a) {
        super(a);
    }

    public void onIteration() {
        super.onIteration();

        for (PaintballPlayer player : arena.getAllPlayers().values()) {
            if (player instanceof DomArenaPlayer) {
                DomArenaPlayer domPlayer = (DomArenaPlayer) player;

                if (domPlayer.isSecuring()) {
                    domPlayer.incrementTimeSecuring();
                    domPlayer.showTimeSecuring();
                }
            }
        }

        if (counter % 10 == 0) {
            for (Team team : arena.getArenaTeamList()) {

                if (arena instanceof DomArena) {
                    int score = ((DomArena) arena).getRunningScores().get(team);

                    while (score > 0) {
                        arena.incrementTeamScore(team);
                        score--;
                    }
                }
            }

            arena.updateAllScoreboard();
        }
    }

    @Override
    public void cancel() {
        super.cancel();

        // TODO: Remove all claiming
    }

    public boolean stop() {
        return arena == null || arena != null && arena.getState() != Arena.ArenaState.IN_PROGRESS;
    }

    public boolean intervalCheck() {
        return true;
    }
}
