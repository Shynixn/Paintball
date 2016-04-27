package me.synapz.paintball.enums;

import org.bukkit.ChatColor;

public enum Messages {

    COMMAND_HOLO_INFO("Remove holograms around you."),
    DOWNLOAD_HOLO("Please download plugin HolographicDisplays to use this feature."),
    HOLO_LINK("http://dev.bukkit.org/bukkit-plugins/holographic-displays/"),
    VALID_RADIUS("Please enter a valid number for radius"),
    HOLOGRAMS_REMOVED("Removed &7%amount% &aholograms."),
    NO_HOLOGRAMS_REMOVED("No holograms were removed."),
    ARENA_REMOVE("Arena has been removed.");

    private final String defaultString;
    private String string;

    Messages(String defaultString) {
        this.defaultString = defaultString;
        this.string = defaultString;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase().replace("_", "-");
    }

    public String getDefaultString() {
        return defaultString;
    }

    public String getString() {
        return string;
    }

    public void setString(String message) {
        this.string = ChatColor.translateAlternateColorCodes('&', message);
    }
}
