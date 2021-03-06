package me.synapz.paintball.scoreboards;

import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.ScoreboardLine;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.utils.MessageBuilder;
import org.bukkit.Bukkit;
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

        for (Team team : pbPlayer.getArena().getActiveArenaTeamList()) {
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
        String title = new MessageBuilder(Messages.SCOREBOARD_TITLE).replace(Tag.TIME, convertToNumberFormat(time)).build();

        objective.setDisplayName(title);
        player.setScoreboard(sb);
    }

    public void setDisplayNameCounter(String prefix, int time) {
        String title = new MessageBuilder(Messages.SCOREBOARD_TITLE).replace(Tag.TIME, convertToNumberFormat(time)).build();
        objective.setDisplayName(prefix + title);
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

    public PaintballScoreboard addLine(ScoreboardLine scoreboardLine, boolean toAdd) {
        if (toAdd) {
            addLine(scoreboardLine);
        }
        return this;
    }

    public PaintballScoreboard addLine(ScoreboardLine sbLine, int startValue) {
        addLine(THEME + new MessageBuilder(sbLine.getMessage()).replace(Tag.AMOUNT, "" + startValue).build());
        return this;
    }

    public PaintballScoreboard addLine(ScoreboardLine sbLine, String startValue) {
        addLine(THEME + new MessageBuilder(sbLine.getMessage()).replace(Tag.AMOUNT, startValue).build());
        return this;
    }

    public PaintballScoreboard addTeams(boolean teamSize) {
        for (Team team : pbPlayer.getArena().getActiveArenaTeamList()) {
            String name = team.getChatColor() + team.getTitleName() + ": " + SECONDARY + (!teamSize ? (pbPlayer.getArena().MAX_SCORE - pbPlayer.getArena().getTeamScore(team)) : team.getSize());
            Score score = objective.getScore(name);
            score.setScore(index);
            lines.put(index, name);
            index++;
        }
        return this;
    }

    public PaintballScoreboard reloadTeams(boolean teamSize) {
        int size = 0;
        for (Team team : pbPlayer.getArena().getActiveArenaTeamList()) {
            String oldValue = lines.get(size);
            String newValue = team.getChatColor() + team.getTitleName() + ": " + SECONDARY + (!teamSize ? (pbPlayer.getArena().MAX_SCORE - pbPlayer.getArena().getTeamScore(team)) : team.getSize());

            sb.resetScores(oldValue);
            Score teamScore = objective.getScore(newValue);
            teamScore.setScore(size);
            lines.replace(size, oldValue, newValue);
            size++;
        }
        updateNametags();
        return this;
    }

    public PaintballScoreboard updateNametags() {
        for (PaintballPlayer pbPlayer : this.pbPlayer.getArena().getAllPlayers().values()) {
            final org.bukkit.scoreboard.Team playerTeam = sb.getTeam(pbPlayer.getTeam().getTitleName());
            playerTeam.setAllowFriendlyFire(false);

            if (!pbPlayer.getArena().NAMETAGS)
                playerTeam.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
            playerTeam.setPrefix(String.valueOf(pbPlayer.getTeam().getChatColor()));
            playerTeam.addPlayer(pbPlayer.getPlayer());
        }
        player.setScoreboard(sb);

        return this;
    }

    public PaintballScoreboard reloadLine(ScoreboardLine sbLine, String score, int line) {
        String oldScore = lines.get(line);
        String newScore = THEME + new MessageBuilder(sbLine.getMessage()).replace(Tag.AMOUNT, score).build();

        if (oldScore == null)
            return this;

        sb.resetScores(oldScore);
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
