package me.synapz.paintball.enums;

import me.synapz.paintball.Message;
import me.synapz.paintball.Utils;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.synapz.paintball.storage.Settings.SECONDARY;
import static me.synapz.paintball.storage.Settings.THEME;

public enum ScoreboardLine {

    KILL_COIN("KillCoins" + Message.SUFFIX),
    KILL_STREAK("Kill Streak" + Message.SUFFIX),
    KILLS("Kills" + Message.SUFFIX),
    KD("K/D" + Message.SUFFIX),
    MONEY("Money" + Message.SUFFIX),
    LINE(SECONDARY + ChatColor.STRIKETHROUGH + Utils.makeSpaces(20)),
    TEAM("Team" + Message.SUFFIX),
    STATUS("Status" + Message.SUFFIX),
    HEALTH("Health" + Message.SUFFIX);

    private String name;

    ScoreboardLine(String name) {
        this.name = THEME + name;
    }

    @Override
    public String toString() {
        return THEME + name + SECONDARY;
    }
}