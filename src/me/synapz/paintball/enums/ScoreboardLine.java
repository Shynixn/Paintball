package me.synapz.paintball.enums;

import me.synapz.paintball.Messenger;
import me.synapz.paintball.Utils;
import org.bukkit.ChatColor;

import static me.synapz.paintball.storage.Settings.SECONDARY;
import static me.synapz.paintball.storage.Settings.THEME;

public enum ScoreboardLine {

    KILL_COIN("KillCoins" + Messenger.SUFFIX),
    KILL_STREAK("Kill Streak" + Messenger.SUFFIX),
    KILLS("Kills" + Messenger.SUFFIX),
    KD("K/D" + Messenger.SUFFIX),
    MONEY("Money" + Messenger.SUFFIX),
    LINE(SECONDARY + ChatColor.STRIKETHROUGH + Utils.makeSpaces(20)),
    TEAM("Team" + Messenger.SUFFIX),
    STATUS("Status" + Messenger.SUFFIX),
    HEALTH("Health" + Messenger.SUFFIX),
    LIVES("Lives" + Messenger.SUFFIX);

    private String name;

    ScoreboardLine(String name) {
        this.name = THEME + name;
    }

    @Override
    public String toString() {
        return THEME + name + SECONDARY;
    }
}