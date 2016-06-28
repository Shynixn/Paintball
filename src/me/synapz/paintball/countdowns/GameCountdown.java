package me.synapz.paintball.countdowns;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.players.ArenaPlayer;

import java.util.ArrayList;
import java.util.List;

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
        Team winningTeam = (Team) arena.getActiveArenaTeamList().toArray()[0]; // just gets the first name as a starting point
        int score = arena.getTeamScore(winningTeam);
        for (Team t : arena.getActiveArenaTeamList()) {
            if (score < arena.getTeamScore(t)) {
                winningTeam = t;
                score = arena.getTeamScore(winningTeam);
            }
        }

        // Checks for ties
        for (Team t : arena.getActiveArenaTeamList()) {
            if (arena.getTeamScore(t) == arena.getTeamScore(winningTeam)) {
                teamsWhoWon.add(t);
            }
        }

        arena.win(teamsWhoWon);
        tasks.remove(arena, this);
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