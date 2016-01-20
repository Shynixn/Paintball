package me.synapz.paintball.countdowns;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.Team;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class GameCountdown extends PaintballCountdown {

    /*
    This Countdown class is responsible for game countdowns (how long an arena lasts)
     */

    public static Map<Arena, GameCountdown> gameCountdowns = new HashMap<>();

    private Arena a;

    public GameCountdown(Arena a) {
        super(a.TIME);
        this.a = a;
        gameCountdowns.put(a, this);
    }

    public void onFinish() {
        Team winningTeam = (Team) a.getArenaTeamList().toArray()[0]; // just gets the first name as a starting point
        int score = a.getTeamScore(winningTeam);
        for (Team t : a.getArenaTeamList()) {
            if (score < a.getTeamScore(t)) {
                winningTeam = t;
                score = a.getTeamScore(winningTeam);
            } else if (score == a.getTeamScore(t) && t != winningTeam) {
                // TODO: it is a tie
            }
        }
        a.win(winningTeam);
        gameCountdowns.remove(a, this);
    }

    public void onIteration() {
        for (ArenaPlayer player : a.getAllArenaPlayers()) {
            player.updateSideScoreboard();
        }
    }

    public boolean stop() {
        return a == null || a != null && a.getState() != Arena.ArenaState.IN_PROGRESS;
    }

    public boolean intervalCheck() {
        return true;
    }
}