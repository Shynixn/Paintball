package me.synapz.paintball.countdowns;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.Team;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

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

        for (ArenaPlayer player : a.getAllArenaPlayers()) {
            player.giveItems();
        }
    }

    public void onFinish() {
        List<Team> teamsWhoWon = new ArrayList<>();
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

        // Checks for ties
        for (Team t : a.getArenaTeamList()) {
            if (a.getTeamScore(t) == a.getTeamScore(winningTeam)) {
                teamsWhoWon.add(t);
            }
        }

        a.win(teamsWhoWon);
        gameCountdowns.remove(a, this);

        // TODO: unreigster all objectives
    }

    public void onIteration() {
        for (ArenaPlayer player : a.getAllArenaPlayers()) {
            player.updateDisplayName();
        }
    }

    public boolean stop() {
        return a == null || a != null && a.getState() != Arena.ArenaState.IN_PROGRESS;
    }

    public boolean intervalCheck() {
        return true;
    }
}