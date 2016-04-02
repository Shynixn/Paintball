package me.synapz.paintball.scoreboards;

import me.synapz.paintball.Team;
import me.synapz.paintball.enums.ScoreboardLine;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.LobbyPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.players.SpectatorPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

import static me.synapz.paintball.storage.Settings.SECONDARY;
import static me.synapz.paintball.storage.Settings.THEME;

public class PaintballScoreboard {

    private static int id = 0;
    private static final String DISPLAY_NAME = THEME + ChatColor.BOLD + "  Paintball " + ChatColor.RESET + SECONDARY +  "%time%  ";

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
        // Registers the Spectator team is the player is a spectator
        if (sb.getTeam("Spectator") == null) {
            org.bukkit.scoreboard.Team specTeam = sb.registerNewTeam("Spectator");
            specTeam.setCanSeeFriendlyInvisibles(true);
        }

        updateNametags();
    }

    public void setDisplayNameCounter(int time) {
        String name = DISPLAY_NAME.replace("%time%", convertToNumberFormat(time));
        objective.setDisplayName(name);
        player.setScoreboard(sb);
    }

    public void setDisplayNameCounter(String prefix, int time) {
        String name = DISPLAY_NAME.replace("%time%", convertToNumberFormat(time));
        objective.setDisplayName(prefix + name);
        player.setScoreboard(sb);
    }

    public PaintballScoreboard addLine(ScoreboardLine sbLine, String startValue, boolean toAdd) {
        if (toAdd)
            return addLine(sbLine, startValue);
        else
            return this;
    }

    public PaintballScoreboard addLine(ScoreboardLine sbLine, int startValue, boolean toAdd) {
        return addLine(sbLine, String.valueOf(startValue), toAdd);
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
        updateNametags();
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

    public PaintballScoreboard updateNametags() {
        for (ArenaPlayer arenaPlayer : pbPlayer.getArena().getAllArenaPlayers()) {
            final org.bukkit.scoreboard.Team playerTeam = sb.getTeam(arenaPlayer.getTeam().getTitleName());
            playerTeam.setAllowFriendlyFire(false);
            playerTeam.setPrefix(String.valueOf(arenaPlayer.getTeam().getChatColor()));
            playerTeam.setSuffix(" " + Settings.THEME + (arenaPlayer.getHealth()) + ChatColor.RED + "❤");
            playerTeam.addPlayer(arenaPlayer.getPlayer());
        }
        for (LobbyPlayer lobbyPlayer : pbPlayer.getArena().getLobbyPlayers()) {
            final org.bukkit.scoreboard.Team playerTeam = sb.getTeam(lobbyPlayer.getTeam().getTitleName());
            playerTeam.setAllowFriendlyFire(false);
            playerTeam.setPrefix(String.valueOf(lobbyPlayer.getTeam().getChatColor()));
            playerTeam.addPlayer(lobbyPlayer.getPlayer());
        }
        for (SpectatorPlayer spectatorPlayer : pbPlayer.getArena().getSpectators()) {
            final org.bukkit.scoreboard.Team playerTeam = sb.getTeam(spectatorPlayer.getTeam().getTitleName());
            playerTeam.setAllowFriendlyFire(false);
            playerTeam.setPrefix(ChatColor.GRAY + String.valueOf(spectatorPlayer.getTeam().getChatColor()));
            playerTeam.addPlayer(spectatorPlayer.getPlayer());
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

    public PaintballScoreboard reloadLine(ScoreboardLine sbLine, String score, int line, boolean condition) {
        if (condition)
            reloadLine(sbLine, score, line);
        return this;
    }

    public PaintballScoreboard build() {
        player.setScoreboard(sb);
        updateNametags();
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
