package me.synapz.paintball;


import com.connorlinfoot.titleapi.TitleAPI;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.synapz.paintball.storage.Settings.*;
import static org.bukkit.ChatColor.*;

public class Message {
    
    // TODO remove static instance, it's really not needed...
    public static final String NO_PERMS = "You don't have access to that command!";
    public static final String SUFFIX = SECONDARY + " » ";
    public static final String EXPIRATION_TIME = createPrefix("Expiration") + SECONDARY + "%time%" + THEME + " seconds";
    public static final String EXPIRATION_END = createPrefix("Expiration") + SECONDARY + "Item %item% has expired";
    public static final String PROTECTION_END = createPrefix("Protection") + SECONDARY + "Protection has expired";
    public static final String PROTECTION_TIME = createPrefix("Protection") + SECONDARY + "%time% " + THEME + "seconds";

    private static Message instance = new Message();
    
    public static Message getMessenger() {
        return instance;
    }

    // Message a player
    // TODO: remove duplicated code
    public void msg(CommandSender sender, boolean titleAPI, ChatColor color, String... msg){
        String[] messages = msg;
        
        for (String string : messages) {
            if (titleAPI && TITLE_API && sender instanceof Player) {
                TitleAPI.sendTitle((Player)sender, 10, 10, 10, PREFIX, color + string);
            }
            sender.sendMessage(PREFIX + color + string);
        }
    }

    // Message a player with or without a prefix
    public void msg(CommandSender sender, boolean prefix, boolean titleAPI, String... msg){
        String[] messages = msg;
        String strPrefix = prefix ? PREFIX : "";
        for (String string : messages) {
            if (titleAPI && TITLE_API && sender instanceof Player) {
                TitleAPI.sendTitle((Player)sender, 10, 10, 10, PREFIX, string);
            }
            sender.sendMessage(strPrefix + string);
        }
    }

    // Checks to see if a player has a permission, returns true if they do false if they don't
    public boolean permissionValidator(Player player, String permission) {
        if (player.hasPermission(permission)) {
            return true;
        } else {
            msg(player, false, RED, NO_PERMS);
            return false;
        }
    }

    // Checks to see if a player has a permission to break/create a sign
    // TODO: make this better!!
    public boolean signPermissionValidator(Player player, String permission) {
        if (player.hasPermission(permission)) {
            return true;
        } else {
            msg(player, false, RED, "You don't have access to create that sign!");
            return false;
        }
    }

    // Sends a message if there is some type of wrong usage
    public void wrongUsage(PaintballCommand command, Player player, Usage usage) {
        if (usage.equals(Usage.TO_MANY_ARGS)) {
            Message.getMessenger().msg(player, false, RED, "To many arguments!", command.getCorrectUsage());
        } else {
            Message.getMessenger().msg(player, false, RED, "Not enough arguments!", command.getCorrectUsage());
        }
    }

    // Get's the help associated with the command type
    public String getHelpTitle(CommandType type) {
        String title = "Paintball";
        if (type == CommandType.ADMIN) {
            title += " Admin";
        } else if (type == CommandType.ARENA) {
            title += " Arena";
        }
        return SECONDARY + STRIKETHROUGH + "                       " + RESET + THEME + " " + title + " " + SECONDARY + STRIKETHROUGH + "                       ";
    }

    private static String createPrefix(String suffix){
        return THEME + BOLD + suffix + SUFFIX;
    }

    public enum Usage {
        TO_MANY_ARGS,
        NOT_ENOUGH_ARGS
    }
}