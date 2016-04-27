package me.synapz.paintball.utils;


import com.connorlinfoot.bountifulapi.BountifulAPI;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.storage.Settings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.synapz.paintball.storage.Settings.*;
import static org.bukkit.ChatColor.*;

public class Messenger {
    
    public static final String NO_PERMS = "You don't have access to that command!";
    public static final String SUFFIX = SECONDARY + " » ";
    public static final String EXPIRATION_TIME = createPrefix("Expiration") + SECONDARY + "%time%" + THEME + " seconds";
    public static final String EXPIRATION_END = createPrefix("Expiration") + SECONDARY + "Item %item% " + SECONDARY + "has expired";
    public static final String PROTECTION_END = createPrefix("Protection") + SECONDARY + "Protection has expired";
    public static final String PROTECTION_TIME = createPrefix("Protection") + SECONDARY + "%time% " + THEME + "seconds";
    public static final String TEAM_SWITCH_TIME = createPrefix("Team Switch") + SECONDARY + "%time%" + THEME + " seconds";
    public static final String TEAM_SWITCH_END = createPrefix("Team Switch") + SECONDARY + "Team switching unlocked";
    public static final String TEAM_SWITCH_ERROR = createPrefix("Team Switch") + SECONDARY + "Wait for the team switch cooldown to end.";

    public static void error(CommandSender sender, Messages...msgs) {
        for (Messages msg : msgs)
            info(sender, RED + msg.getString());
    }

    public static void error(CommandSender sender, String... msg) {
        for (String str : msg)
            info(sender, RED + str);
    }

    public static void success(CommandSender sender, Messages msg) {
        info(sender, GREEN + msg.getString());
    }

    public static void success(CommandSender sender, String... msg) {
        for (String str : msg)
            info(sender, GREEN + str);
    }

    public static void info(CommandSender sender, String...msg) {
        for (String str : msg)
            sender.sendMessage(Settings.PREFIX + Settings.THEME + str);
    }

    public static void msg(CommandSender sender, String...msg) {
        for (String str : msg)
            sender.sendMessage(str);
    }

    public static void titleMsg(CommandSender sender, boolean inText, String msg) {
        if (inText)
            info(sender, msg);

        if (TITLE && sender instanceof Player)
            BountifulAPI.sendTitle((Player)sender, 10, 10, 10, PREFIX, msg);
    }

    // Checks to see if a player has a permission, returns true if they do false if they don't
    public static boolean permissionValidator(Player player, String permission) {
        if (player.hasPermission(permission)) {
            return true;
        } else {
            error(player, NO_PERMS);
            return false;
        }
    }

    // Checks to see if a player has a permission to break/create a sign
    public static boolean signPermissionValidator(Player player, String permission) {
        if (player.hasPermission(permission)) {
            return true;
        } else {
            error(player, "You don't have access for that sign!");
            return false;
        }
    }

    // Sends a message if there is some type of wrong usage
    public static void wrongUsage(PaintballCommand command, Player player, Usage usage) {
        if (usage.equals(Usage.TO_MANY_ARGS)) {
            error(player, "To many arguments!", command.getCorrectUsage());
        } else {
            error(player, "Not enough arguments!", command.getCorrectUsage());
        }
    }

    // Get's the help associated with the command type
    public static String getHelpTitle(CommandType type) {
        String title = BOLD + "Paintball";
        if (type == CommandType.ADMIN) {
            title += " Admin";
        } else if (type == CommandType.ARENA) {
            title += " Arena";
        }
        return SECONDARY + STRIKETHROUGH + Utils.makeSpaces(23) + RESET + DARK_GRAY + BOLD + STRIKETHROUGH + "[-" + RESET + THEME + " " + BOLD + title + " " + DARK_GRAY + BOLD + STRIKETHROUGH + "-]" + SECONDARY + STRIKETHROUGH + Utils.makeSpaces(23);
    }

    public static String createPrefix(String suffix){
        return THEME + BOLD + suffix + SUFFIX;
    }

    public enum Usage {
        TO_MANY_ARGS,
        NOT_ENOUGH_ARGS
    }
}