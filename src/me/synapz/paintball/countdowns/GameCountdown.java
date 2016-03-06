package me.synapz.paintball.countdowns;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Team;
import me.synapz.paintball.players.ArenaPlayer;

import java.util.*;

public class GameCountdown extends PaintballCountdown {

    /*
    This Countdown class is responsible for game countdowns (how long an arena lasts)
     */

    public GameCountdown(Arena a) {
        super(a, a.TIME);

        for (ArenaPlayer player : arena.getAllArenaPlayers()) {
            player.giveItems();
        }
    }

    public void onFinish() {
        arena.updateSigns();
        List<Team> teamsWhoWon = new ArrayList<>();
        Team winningTeam = (Team) arena.getArenaTeamList().toArray()[0]; // just gets the first name as a starting point
        int score = arena.getTeamScore(winningTeam);
        for (Team t : arena.getArenaTeamList()) {
            if (score < arena.getTeamScore(t)) {
                winningTeam = t;
                score = arena.getTeamScore(winningTeam);
            } else if (score == arena.getTeamScore(t) && t != winningTeam) {
                // TODO: it is a tie
            }
        }

        // Checks for ties
        for (Team t : arena.getArenaTeamList()) {
            if (arena.getTeamScore(t) == arena.getTeamScore(winningTeam)) {
                teamsWhoWon.add(t);
            }
        }

        arena.win(teamsWhoWon);
        tasks.remove(arena, this);

        // TODO: unreigster all objectives
    }

    public void onIteration() {
        arena.updateSigns();
        arena.updateAllScoreboardTimes();
    }

    @Override
    public void cancel() {
        super.cancel();
        arena.updateSigns();
    }

    public boolean stop() {
        return arena == null || arena != null && arena.getState() != Arena.ArenaState.IN_PROGRESS;
    }

    public boolean intervalCheck() {
        return true;
    }
}