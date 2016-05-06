package me.synapz.paintball.enums;

import org.bukkit.ChatColor;

public enum Messages {

    COMMAND_ARENA_INFO("Display all Paintball Arena setup commands"),
    COMMAND_CREATE_INFO("Create a new Arena"),
    COMMAND_SETFLAG_INFO("Set Arena flag point"),
    COMMAND_DELFLAG_INFO("Delete Arena flag point"),
    COMMAND_SETLOCATION_INFO("Set Arena location"),
    COMMAND_DELLOCATION_INFO("Delete Arena location"),
    COMMAND_SETSPECTATE_INFO("Set Arena spectate location"),
    COMMAND_DELSPECTATE_INFO("Delete Arena spectate location"),
    COMMAND_REMOVE_INFO("Remove an arena"),
    COMMAND_RENAME_INFO("Rename an Arena"),
    COMMAND_SETMAX_INFO("Set max number of players"),
    COMMAND_SETMIN_INFO("Set min amount of players"),
    COMMAND_SETTEAMS_INFO("Set teams via ChatColors seperated by commas ex. &1,&b,&c", false),
    COMMAND_STEPS_INFO("List steps of an Arena"),


    COMMAND_CONVERT_INFO("Convert an Arena to a different type"),
    COMMAND_DISABLE_INFO("Disable an Arena"),
    COMMAND_ENABLE_INFO("Enable an Arena"),
    COMMAND_INFO_INFO("Display Arena information"),
    COMMAND_RELOAD_INFO("Reloads all yml files"),
    COMMAND_RESET_INFO("Reset a player's stats"),
    COMMAND_START_INFO("Force start an Arena"),
    COMMAND_STOP_INFO("Force stop an Arena"),
    COMMAND_ADMIN_INFO("Display all Paintball Admin commands"),
    COMMAND_SETHOLO_INFO("Creates a leaderboard hologram"),
    COMMAND_HOLO_INFO("Remove holograms around you"),

    COMMAND_JOIN_INFO("Join an Arena"),
    COMMAND_LEAVE_INFO("Leave an Arena"),
    COMMAND_LIST_INFO("List of all Arenas"),
    COMMAND_SPECTATE_INFO("Spectate an Arena"),
    COMMAND_STATS_INFO("View player's game statistics"),
    COMMAND_TOP_INFO("View leaderboards."),

    DOWNLOAD_HOLO("Please download plugin HolographicDisplays to use this feature."),
    HOLO_LINK("http://dev.bukkit.org/bukkit-plugins/holographic-displays/"),
    VALID_RADIUS("Please enter a valid number for radius"),
    HOLOGRAMS_REMOVED("Removed &7" + Tag.AMOUNT + " &aholograms."),
    NO_HOLOGRAMS_REMOVED("No holograms were removed."),
    NO_ARENAS("No arenas are currently opened."),
    NOT_IN_ARENA("You are not in an arena."),
    LEFT_ARENA("Successfully left arena."),
    ARENA_NOT_SETUP(Tag.ARENA + " has not been fully setup."),
    ARENA_DISABLED(Tag.ARENA + " is disabled."),
    ARENA_NOT_IN_PROGRESS(Tag.ARENA + " is currently not in progress, nothing to spectate."),
    IN_ARENA("You are already in an arena!"),
    PAGE_REAL_NUMBER("Please specify a real number as the page."),
    PAGE_BIGGER("The page cannot be lower than 0"),
    PAGE_FIND_ERROR("Page " + Tag.AMOUNT + "/" + Tag.MAX + " cannot be found."),
    ARENA_CREATE(Tag.ARENA + " successfully created!/n" + Tag.STEPS),
    ARENA_NAME_EXISTS("An arena named" + Tag.ARENA + " already exists!"),
    ARENA_REMOVE("Arena has been removed."),
    ARENA_CONVERT_SAME_TYPE(Tag.ARENA + " is already a " + Tag.ARENA_TYPE + " arena."),
    ARENA_CONVERT_SUCCESS(Tag.ARENA + " has been converted to " + Tag.ARENA_TYPE),
    INVALID_ARENA(Tag.ARENA + " is an invalid arena."),
    INVALID_TEAM(Tag.TEAM + " is an invalid team. Choose either <" + Tag.TEAMS + ">"),
    INVALID_STAT(Tag.STAT + " is an invalid statistic. Choose either " + Tag.STATS),
    INVALID_COMMAND("Unknown Command! Type /paintball for a list of commands."),
    INTERNAL_ERROR("An internal error occurred: " + Tag.ERROR),
    NO_CONSOLE_PERMISSION("Console does not have access to that command!"),
    NO_TEAMS_SET(Tag.ARENA + " does not have any teams set!"),
    INVALID_ARENA_TYPE("Invalid arena type. Choose either <" + Tag.ARENA_TYPES + ">"),
    CHOOSE_ENABLE_OR_DISABLE(Tag.COMMAND + " is an invalid choice. Use either enable/disable"),
    DISABLE_SUCCESS(Tag.ARENA + " has been disabled!"),
    ENABLE_SUCCESS(Tag.ARENA + " has been enabled!"),
    ARENA_ENABLED(Tag.ARENA + " is already enabled."),

    SCOREBOARD_COINS("Coins &7» " + Tag.AMOUNT),
    SCOREBOARD_KILLS("Kills &7» " + Tag.AMOUNT),
    SCOREBOARD_KILL_STREAK("Kill Streak &7» " + Tag.AMOUNT),
    SCOREBOARD_KD("K/D &7» " + Tag.AMOUNT),
    SCOREBOARD_MONEY("Money &7» " + Tag.AMOUNT),
    SCOREBOARD_LINE("&7&m                    "),
    SCOREBOARD_STATUS("Status &7» " + Tag.AMOUNT),
    SCOREBOARD_LIVES("Lives &6» " + Tag.AMOUNT),
    SCOREBOARD_MODE("Mode &7» " + Tag.AMOUNT),
    SCOREBOARD_HEALTH("Health &7» " + Tag.AMOUNT),
    SCOREBOARD_TEAM("Team &7» " + Tag.AMOUNT);

    private final String defaultString;
    private String string;
    private boolean parseChatColor = true;

    Messages(String defaultString) {
        this.defaultString = defaultString;
        this.string = defaultString;
    }

    Messages(String defaultString, boolean parseChatColor) {
        this(defaultString);
        this.parseChatColor = parseChatColor;
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
        this.string = parseChatColor ? ChatColor.translateAlternateColorCodes('&', message) : message;
    }
}