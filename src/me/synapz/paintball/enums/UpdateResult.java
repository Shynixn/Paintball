package me.synapz.paintball.enums;

import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;

public enum UpdateResult {

    UPDATE("Version " + ChatColor.GRAY + "%new%" + ChatColor.GREEN + " is out! " + Settings.SECONDARY + ChatColor.ITALIC + ChatColor.UNDERLINE + Settings.WEBSITE),
    ERROR(ChatColor.RED + "There was an error checking for an update."),
    DISABLED(""),
    NO_UPDATE("You have the latest version of the plugin.");

    private String message;

    UpdateResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
