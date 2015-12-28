package me.synapz.paintball.enums;

import java.util.UUID;

public enum StatType {
    // Automatically calculated
    HIGEST_KILL_STREAK("Highest Killstreak", ".Highest-Kill-Streak", "killstreak"),

    KD("K/D", "none", "kd"),
    KILLS("Kills", ".Kills", "kills"), // added
    DEATHS("Deaths", ".Deaths", "deaths"), // added

    ACCURACY("Accuracy", "none", "accuracy"),
    SHOTS("Shots", ".Shots", "shots"), // added
    HITS("Hits", ".Hits", "hits"), // added

    GAMES_PLAYED("Games Played", ".Games-Played", "gamesplayed"), // added
    WINS("Wins", ".Wins", "wins"), // added
    DEFEATS("Defeats", ".Defeats", "defeats"); // added


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
}
