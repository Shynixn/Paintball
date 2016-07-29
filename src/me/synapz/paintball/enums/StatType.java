package me.synapz.paintball.enums;

import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.entity.Player;

public enum StatType {

    HIGEST_KILL_STREAK(Messages.HIGEST_KILL_STREAK, "Highest-Kill-Streak", "killstreak"),

    KD(Messages.KD, "none", "kd"),
    KILLS(Messages.KILLS, "Kills", "kills"),
    DEATHS(Messages.DEATHS, "Deaths", "deaths"),

    ACCURACY(Messages.ACCURACY, "none", "accuracy", "%"),
    SHOTS(Messages.SHOTS, "Shots", "shots"),
    HITS(Messages.HITS, "Hits", "hits"),

    GAMES_PLAYED(Messages.GAMES_PLAYED, "Games-Played", "gamesplayed"),
    WINS(Messages.WINS, "Wins", "wins"),
    DEFEATS(Messages.DEFEATS, "Defeats", "defeats"),
    TIES(Messages.TIES, "Ties", "ties"),

    FLAGS_CAPTURED(Messages.FLAGS_CAPTURED, "Flags-Captured", "flagscaptured"),
    FLAGS_DROPPED(Messages.FLAGS_DROPPED, "Flags-Dropped", "flagsdropped"),

    TIME_PLAYED(Messages.TIME_PLAYED, "Time-Played", "timeplayed", "s");

    private Messages message;
    String name;
    private String path;
    private String sign;
    private String suffix = "";

    StatType(Messages message, String path, String signName) {
        this.message = message;
        this.path = path;
        this.sign = signName;
    }

    StatType(Messages message, String path, String signName, String suffix) {
        this(message, path, signName);
        this.suffix = suffix;
    }

    public String getRawPath() {
        return this.path;
    }

    public String getPath() {
        return this.path;
    }

    public String getName() {
        return name;
    }

    public String getSignName() {
        return this.sign;
    }

    public String getSuffix() {
        return suffix;
    }

    public static String getReadableList() {
        StringBuilder values = new StringBuilder();

        for (StatType stat : StatType.values()) {
            values.append(stat.getSignName() + ", ");
        }
        values.replace(values.lastIndexOf(","), values.length()-1, "");
        return values.toString();
    }

    // useful for calculated stats like KD and Accuracy, which have their own method instead of being stored in config
    public boolean isCalculated() {
        return path.equals("none");
    }

    @Override
    public String toString() {
        return name;
    }

    private void loadMessage() {
        this.name = message.getString();
    }

    public static void loadStatNames() {
        for (StatType type : StatType.values())
            type.loadMessage();
    }

    public static StatType getStatType(Player player, String statString) {
        StatType type = null;
        for (StatType t : StatType.values()) {
            if (t.getSignName().equalsIgnoreCase(statString) || t.getName().equals(statString)) {
                type = t;
            }
        }

        if (type == null) {
            if (player != null)
                Messenger.error(player, new MessageBuilder(Messages.INVALID_STAT).replace(Tag.STAT, statString).replace(Tag.STATS, StatType.getReadableList()).build());
        }
        return type;
    }
}