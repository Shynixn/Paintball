package me.synapz.paintball.enums;

import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.entity.Player;

import java.util.UUID;

public enum StatType {

    HIGEST_KILL_STREAK("Highest Killstreak", ".Highest-Kill-Streak", "killstreak"),

    KD("K/D", "none", "kd"),
    KILLS("Kills", ".Kills", "kills"),
    DEATHS("Deaths", ".Deaths", "deaths"),

    ACCURACY("Accuracy", "none", "accuracy"),
    SHOTS("Shots", ".Shots", "shots"),
    HITS("Hits", ".Hits", "hits"),

    GAMES_PLAYED("Games Played", ".Games-Played", "gamesplayed"),
    WINS("Wins", ".Wins", "wins"),
    DEFEATS("Defeats", ".Defeats", "defeats"),
    TIES("Ties", ".Ties", "ties"),

    FLAGS_CAPTURED("Flags Captured", ".Flags-Captured", "flagscaptured"),
    FLAGS_DROPPED("Flags Dropped", ".Flags-Dropped", "flagsdropped"),

    TIME_PLAYED("Time Played", ".Time-Played", "timeplayed");

    private String name;
    private String path;
    private String sign;

    StatType(String name, String path, String signName) {
        this.name = name;
        this.path = path;
        this.sign = signName;
    }

    public String getPath(UUID id) {
        return "Player-Data." + id + ".Stats" + this.path;
    }

    public String getName() {
        return this.name;
    }

    public String getSignName() {
        return this.sign;
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