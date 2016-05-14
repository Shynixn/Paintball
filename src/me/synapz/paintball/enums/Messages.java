package me.synapz.paintball.enums;

import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;

import static me.synapz.paintball.storage.Settings.SECONDARY;
import static me.synapz.paintball.storage.Settings.THEME;
import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;

public enum Messages {

    PREFIX("&8[&3Paintball&8] "),

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
    CANNOT_JOIN("Error: arena is not available to join!"),
    LEFT_ARENA("Successfully left arena."),
    ARENA_NOT_SETUP(Tag.ARENA + " has not been fully setup."),
    ARENA_DISABLED(Tag.ARENA + " is disabled."),
    ARENA_NOT_IN_PROGRESS(Tag.ARENA + " is currently not in progress"),
    IN_ARENA("You are already in an arena!"),
    ARENA_NOT_FOUND("No arena named " + Tag.ARENA + " found."),
    PAGE_REAL_NUMBER("Please specify a real number as the page."),
    PAGE_BIGGER("The page cannot be lower than 0"),
    PAGE_FIND_ERROR("Page " + Tag.AMOUNT + "/" + Tag.MAX + " cannot be found."),
    ARENA_CREATE(Tag.ARENA + " successfully created!/n" + Tag.STEPS),
    ARENA_NAME_EXISTS("An arena named" + Tag.ARENA + " already exists!"),
    ARENA_REMOVE("Arena has been removed."),
    ARENA_FORCE_STOPPED(Tag.ARENA + " has been force stopped!"),
    ARENA_CONVERT_SAME_TYPE(Tag.ARENA + " is already a " + Tag.ARENA_TYPE + " arena."),
    ARENA_CONVERT_SUCCESS(Tag.ARENA + " has been converted to " + Tag.ARENA_TYPE),
    INVALID_ARENA(Tag.ARENA + " is an invalid arena."),
    INVALID_TEAM(Tag.TEAM + " is an invalid team. Choose either <" + Tag.TEAMS + ">"),
    INVALID_STAT(Tag.STAT + " is an invalid statistic. Choose either " + Tag.STATS),
    INVALID_COMMAND("Unknown Command! Type /paintball for a list of commands."),
    INTERNAL_ERROR("An internal error has occurred: %error%"),
    NO_PERMISSION("You don't have access to that command!"),
    NO_CONSOLE_PERMISSION("Console does not have access to that command!"),
    NO_TEAMS_SET(Tag.ARENA + " does not have any teams set!"),
    INVALID_ARENA_TYPE("Invalid arena type. Choose either <" + Tag.ARENA_TYPES + ">"),
    CHOOSE_ENABLE_OR_DISABLE(Tag.COMMAND + " is an invalid choice. Use either enable/disable"),
    DISABLE_SUCCESS(Tag.ARENA + " has been disabled!"),
    ENABLE_SUCCESS(Tag.ARENA + " has been enabled!"),
    ARENA_ENABLED(Tag.ARENA + " is already enabled."),
    ARENA_IS_FINISHED("Game is already finished."),
    ARENA_DEFAULT_ACTION("shot"),
    ARENA_START_COUNTDOWN_HEADER("&aStarting"),
    ARENA_START_COUNTDOWN_FOOTER("&7" + Tag.TIME + "&a seconds!"),
    ARENA_LOBBY_COUNTDOWN_HEADER("&aWaiting"),
    ARENA_LOBBY_COUNTDOWN_FOOTER("&7" + Tag.TIME + "&a seconds!"),
    ARENA_MOVE_ERROR("You are not allowed to move items in your inventory!"),
    ARENA_SHOP_NAME("&6Coin Shop"),
    ARENA_NO_DUEL_WIELD("You are not allowed to duel wield!"),
    TELEPORTING("&aTeleporting into arena..."),
    ARENA_FLAG_DROP("&lThe " + Tag.SECONDARY + Tag.TEAM + Tag.THEME + " team has dropped the flag!"),
    ARENA_FLAG_SCORE("&lThe " + Tag.SECONDARY + Tag.TEAM + Tag.THEME + " team has scored a flag!"),
    ARENA_FLAG_STEAL("&lPlayer " + Tag.SECONDARY + Tag.SENDER + Tag.THEME + " has stolen " + Tag.SECONDARY + Tag.TEAM + Tag.THEME + "'s flag!"),
    ARENA_JOIN_MESSAGE(Tag.TEAM_COLOR + "" + Tag.SENDER + "&a has joined the arena! &7" + Tag.AMOUNT + "/" + Tag.MAX),
    ARENA_YOU_JOINED("&aYou have joined the arena!"),
    ARENA_JOINED("&aJoined arena"),
    ARENA_SIZE("&7" + Tag.AMOUNT + "/" + Tag.MAX),
    ARENA_TEAM_CHANGE("&aYou are now on the " + Tag.TEAM_COLOR + "" + Tag.TEAM + " Team!"),
    ARENA_CANNOT_PICKUP_FLAG("You cannot pickup your own team's flag!"),
    ARENA_CANNOT_BREAK_BLOCKS("You are not allowed to break blocks while in the arena!"),
    ARENA_COMMAND_DISABLED("That command is disabled while in the arena."),

    SCOREBOARD_TITLE(Tag.THEME + "&l  Paintball &f" + Tag.SECONDARY + "%time%  "),
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
    SCOREBOARD_TEAM("Team &7» " + Tag.AMOUNT),

    SIGN_WRONG_SYNTAX("Wrong syntax for creating Paintball sign."),
    SIGN_AUTOJOIN_CREATED("Auto Join sign successfully created!"),
    SIGN_JOIN_CREATED("Join sign successfully created!"),
    SIGN_LEAVE_CREATED("Leave sign successfully created!"),
    SIGN_LEADERBOARD_REMOVED("Leaderboard sign has been successfully removed!"),
    SIGN_LEAVE_REMOVED("Leave sign has been successfully removed!"),
    SIGN_AUTOJOIN_REMOVED("Autojoin sign has been successfully removed!"),
    SIGN_JOIN_REMOVED(Tag.ARENA + "'s join sign has been successfully removed!"),
    SKULL_LEADERBOARD_REMOVED("Leaderboard skull has been successfully removed!"),

    ARENA_START_MESSAGE("Game started"),
    ARENA_TEAMS_NOT_BALANCED("You cannot change to this team until the teams are balanced."),

    KILL_CONFIRMED("&e&lKill Confirmed!"),
    KILL_DENIED("&c&lKill Denied!"),

    COIN_ITEM_LORE_LAYOUT(Tag.DESCRIPTION + "/n" + Tag.LASTS + "/n" + Tag.COINS + "/n" + Tag.COST + "/n" + Tag.ERROR),
    COIN_ITEM_DESCRIPTION(Tag.THEME + "Description: " + Tag.SECONDARY + Tag.DESCRIPTION),
    COIN_ITEM_LAST(Tag.THEME + "Lasts: " + Tag.SECONDARY + Tag.TIME + " seconds"),
    COIN_ITEM_COST(Tag.THEME + "Cost: " + Tag.SECONDARY + Tag.CURRENCY + Tag.AMOUNT),
    COIN_ITEM_COINS(Tag.THEME + "Coins: " + Tag.SECONDARY + Tag.AMOUNT),
    COIN_ITEM_ERROR_1("You don't have permission to use this item!"),
    COIN_ITEM_ERROR_2("You don't have enough coins!"),
    COIN_ITEM_ERROR_3("You don't have enough money!");

    ;

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