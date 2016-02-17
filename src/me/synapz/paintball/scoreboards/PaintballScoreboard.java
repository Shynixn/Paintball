package me.synapz.paintball.scoreboards;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.Team;
import me.synapz.paintball.countdowns.GameCountdown;
import me.synapz.paintball.enums.ScoreboardLine;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.LobbyPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.players.SpectatorPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.synapz.paintball.storage.Settings.SECONDARY;
import static me.synapz.paintball.storage.Settings.THEME;

public class PaintballScoreboard {

    private static int id = 0;
    private static final String DISPLAY_NAME = THEME + ChatColor.BOLD + "     Paintball " + ChatColor.RESET + SECONDARY +  "%time%     ";

    private final PaintballPlayer pbPlayer;
    private final Player player;
    private final Scoreboard sb;
    private final Objective objective;

    private int index = 0;
    private Map<Integer, String> lines = new HashMap<>();

    public PaintballScoreboard(PaintballPlayer pbPlayer, int startTime, String objectiveName) {
        this.pbPlayer = pbPlayer;
        this.player = pbPlayer.getPlayer();
        this.sb = Bukkit.getScoreboardManager().getNewScoreboard();

        objective = sb.registerNewObjective(objectiveName + id, "dummy");
        setDisplayNameCounter(startTime);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        id++;

        for (Team team : pbPlayer.getArena().getArenaTeamList()) {
            if (sb.getTeam(team.getTitleName()) == null)
                sb.registerNewTeam(team.getTitleName());
        }
        updateNametags();
    }

    public void setDisplayNameCounter(int time) {
        String name = DISPLAY_NAME.replace("%time%", convertToNumberFormat(time));
        objective.setDisplayName(name);
        player.setScoreboard(sb);
    }

    public PaintballScoreboard addLine(ScoreboardLine sbLine) {
        addLine(sbLine.toString());
        return this;
    }

    public PaintballScoreboard addLine(ScoreboardLine sbLine, int startValue) {
        addLine(sbLine.toString() + SECONDARY + startValue);
        return this;
    }

    public PaintballScoreboard addLine(ScoreboardLine sbLine, String startValue) {
        addLine(sbLine.toString() + SECONDARY + startValue);
        return this;
    }

    public PaintballScoreboard addTeams(boolean asArenaPlayer) {
        for (Team team : pbPlayer.getArena().getArenaTeamList()) {
            String name = team.getChatColor() + team.getTitleName() + ": " + SECONDARY + (asArenaPlayer ? (pbPlayer.getArena().MAX_SCORE - pbPlayer.getArena().getTeamScore(team)) : team.getSize());
            Score score = objective.getScore(name);
            score.setScore(index);
            lines.put(index, name);
            index++;
        }
        return this;
    }

    public PaintballScoreboard reloadTeams(boolean asArenaPlayer) {
        int size = 0;
        for (Team team : pbPlayer.getArena().getArenaTeamList()) {
            String oldValue = lines.get(size);
            String newValue = team.getChatColor() + team.getTitleName() + ": " + SECONDARY + (asArenaPlayer ? (pbPlayer.getArena().MAX_SCORE - pbPlayer.getArena().getTeamScore(team)) : team.getSize());

            sb.resetScores(Bukkit.getOfflinePlayer(oldValue));
            Score teamScore = objective.getScore(newValue);
            teamScore.setScore(size);
            lines.replace(size, oldValue, newValue);
            size++;
        }
        updateNametags();
        return this;
    }

    private PaintballScoreboard updateNametags() {
        for (PaintballPlayer player : pbPlayer.getArena().getAllPlayers().values()) {
            final org.bukkit.scoreboard.Team playerTeam = sb.getTeam(player.getTeam().getTitleName());
            playerTeam.setAllowFriendlyFire(false);
            playerTeam.setPrefix(String.valueOf(player.getTeam().getChatColor()));
            playerTeam.addPlayer(player.getPlayer());
        }
        player.setScoreboard(sb);
        return this;
    }

    public PaintballScoreboard reloadLine(ScoreboardLine sbLine, String score, int line) {
        String oldScore = lines.get(line);
        String newScore = sbLine.toString() + score;

        sb.resetScores(Bukkit.getOfflinePlayer(oldScore));
        Score objScore = objective.getScore(newScore);
        objScore.setScore(line);
        lines.replace(line, lines.get(line), newScore);
        player.setScoreboard(sb);
        return this;
    }

    public PaintballScoreboard build() {
        player.setScoreboard(sb);
        return this;
    }

    public static String convertToNumberFormat(int time) {
        int minutes = time/60;
        int seconds = time%60;
        return String.format("%d:" + (seconds < 10 ? "0" : "") + "%d", minutes, seconds);
    }

    private void addLine(String line) {
        line = lines.values().contains(line) ? line + " " : line;
        Score score = objective.getScore(line);
        score.setScore(index);

        lines.put(index, line);
        index++;
    }
}
