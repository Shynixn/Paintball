package me.synapz.paintball.countdowns;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.DomArena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.players.DomArenaPlayer;
import me.synapz.paintball.players.PaintballPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        if (counter % ((DomArena) arena).UPDATE_INTERVAL == 0) {
            List<Team> winningTeams = new ArrayList<>();

            for (Team team : arena.getArenaTeamList()) {

                if (arena instanceof DomArena) {
                    int score = ((DomArena) arena).getRunningScores().get(team);

                    while (score > 0) {
                        arena.incrementTeamScore(team, false);
                        score--;

                        // Do the win check AFTER we increment all scores in case there was a tie
                        if (arena.getTeamScore(team) == arena.MAX_SCORE)
                            winningTeams.add(team);
                    }
                }
            }

            // Now if the winningTeams is not empty we will determine the winners, or tiers
            if (!winningTeams.isEmpty())
                arena.win(winningTeams);

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
