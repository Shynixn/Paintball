package me.synapz.paintball;


import com.connorlinfoot.titleapi.TitleAPI;
import me.synapz.paintball.commands.Command;
import me.synapz.paintball.storage.Settings;
import static org.bukkit.ChatColor.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class Message {
    
    // TODO remove static, it's really not needed...
    // determined default values
    public static final String NO_PERMS = "You don't have access to that command!";
    
    private static Message instance = new Message();
    
    public static Message getMessenger() {
        return instance;
    }

    // TODO: remove duplicated code
    public void msg(CommandSender sender, boolean titleAPI, ChatColor color, String... msg){
        String[] messages = msg;
        
        for (String string : messages) {
            if (titleAPI && Settings.TITLE_API && sender instanceof Player) {
                TitleAPI.sendTitle((Player)sender, 10, 10, 10, Settings.getSettings().getPrefix(), color + string);
            }
            sender.sendMessage(Settings.getSettings().getPrefix() + color + string);
        }
    }

    public void msg(CommandSender sender, boolean prefix, boolean titleAPI, String... msg){
        String[] messages = msg;
        String strPrefix = prefix ? Settings.getSettings().getPrefix() : "";
        for (String string : messages) {
            if (titleAPI && Settings.TITLE_API && sender instanceof Player) {
                TitleAPI.sendTitle((Player)sender, 10, 10, 10, Settings.getSettings().getPrefix(), string);
            }
            sender.sendMessage(strPrefix + string);
        }
    }
    
    public boolean permissionValidator(Player player, String permission) {
        if (player.hasPermission(permission)) {
            return true;
        } else {
            msg(player, false, RED, NO_PERMS);
            return false;
        }
    }

    // TODO: make this better!!
    public boolean signPermissionValidator(Player player, String permission) {
        if (player.hasPermission(permission)) {
            return true;
        } else {
            msg(player, false, RED, "You don't have access to create that sign!");
            return false;
        }
    }
    
    public void wrongUsage(Command command, Player player, Usage usage) {
        if (usage.equals(Usage.TO_MANY_ARGS)) {
            Message.getMessenger().msg(player, false, RED, "To many arguments!", command.getCorrectUsage(command));
        } else {
            Message.getMessenger().msg(player, false, RED, "Not enough arguments!", command.getCorrectUsage(command));
        }
    }
    
    public String getHelpTitle(Command.CommandType type) {
        String title = "Paintball";
        if (type == Command.CommandType.ADMIN) {
            title += " Admin";
        } else if (type == Command.CommandType.ARENA) {
            title += " Arena";
        }
        return Settings.getSettings().getSecondaryColor() + STRIKETHROUGH + "                    " + RESET + Settings.getSettings().getTheme() + " " + title + " " + Settings.getSettings().getSecondaryColor() + STRIKETHROUGH + "                    ";
    }
    
    public enum Usage {
        TO_MANY_ARGS,
        NOT_ENOUGH_ARGS;
    }
}