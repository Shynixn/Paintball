package me.synapz.paintball;

import java.util.UUID;

public enum StatType {
    KD("K/D", ".K-D", "kd"),
    KILLS("Kills", ".Kills", "kills"),
    DEATHS("Deaths", ".Deaths", "deaths"),
    HIGEST_KILL_STREAK("Highest Killstreak", ".Highest-Kill-Streak", "killstreak"),
    GAMES_PLAYED("Games Played", ".Games-Played", "gamesplayed"),
    WINS("Wins", ".Wins", "wins"),
    DEFEATS("Defeats", ".Defeats", "defeats"),
    SHOTS("Shots", ".Shots", "shots"),
    HITS("Hits", ".Hits", "hits"),
    ACCURACY("Accuracy", ".Accuracy", "accuracy");

    private String name;
    private String path;
    private String sign;

    StatType(String name, String path, String signName) {
        this.name = name;
        this.path = path;
        this.sign = signName;
    }

    public String getPath(UUID id) {
        return "Stats." + id + this.path;
    }

    public String getPath(String id) {
        return "Stats." + id + this.path;
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
}
