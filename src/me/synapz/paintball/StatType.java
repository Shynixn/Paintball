package me.synapz.paintball;

import java.util.UUID;

public enum StatType {
    KD("K/D", ".K-D", "kd"),
    KILLS("Kills", ".Kills", "kills"),
    DEATHS("Deaths", ".Deaths", "deaths"),
    HIGEST_KILL_STREAK("Highest Killstreak", ".Highest-Kill-Streak", "killstreak"),
    GAMES_PLAYED("Games Played", ".Games-Played", "gamesplayed"),
    WON("Won", ".Won", "won"),
    LOST("Lost", ".Lost", "lost");

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
}
