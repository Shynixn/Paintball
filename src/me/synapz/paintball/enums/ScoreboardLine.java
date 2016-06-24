package me.synapz.paintball.enums;

import static me.synapz.paintball.storage.Settings.SECONDARY;
import static me.synapz.paintball.storage.Settings.THEME;

public enum ScoreboardLine {

    COIN(Messages.SCOREBOARD_COINS),
    KILL_STREAK(Messages.SCOREBOARD_KILL_STREAK),
    KILLS(Messages.SCOREBOARD_KILLS),
    KD(Messages.SCOREBOARD_KD),
    MONEY(Messages.SCOREBOARD_MONEY),
    LINE(Messages.SCOREBOARD_LINE),
    TEAM(Messages.SCOREBOARD_TEAM),
    STATUS(Messages.SCOREBOARD_STATUS),
    HEALTH(Messages.SCOREBOARD_HEALTH),
    LIVES(Messages.SCOREBOARD_LIVES),
    MODE(Messages.SCOREBOARD_MODE),
    PLAYERS(Messages.SCOREBOARD_PLAYERS),
    WAGER(Messages.SCOREBOARD_WAGER);

    private Messages message;
    private String name;

    ScoreboardLine(Messages message) {
        this.message = message;
    }

    public Messages getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return THEME + name + SECONDARY;
    }

    private void loadMessage() {
        this.name = THEME + message.getString();
    }

    public static void loadScoreboardLines() {
        for (ScoreboardLine line : ScoreboardLine.values())
            line.loadMessage();
    }
}