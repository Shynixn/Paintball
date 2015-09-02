package me.synapz.paintball;


import me.synapz.paintball.commands.Command;
import me.synapz.paintball.storage.Settings;
import static org.bukkit.ChatColor.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class Message {


    // determined default values
    public static final String NO_PERMS = "You don't have access to that command!";

    private static Message instance = new Message();

    public static Message getMessenger() {
        return instance;
    }

    public void msg(CommandSender sender, ChatColor color, String... msg){
        String[] messages = msg;

        for (String string : messages) {
            sender.sendMessage(Settings.getSettings().getPrefix() + color + string);
        }
    }

    public void debug(String... msg) {
        if (Settings.DEBUG) {
            for (String string : msg) {
                Bukkit.getPluginManager().getPlugin("Paintball").getLogger().log(Level.INFO, string);
            }
        }
    }

    public boolean permissionValidator(Player player, String permission) {
        if (player.hasPermission(permission)) {
            return true;
        } else {
            msg(player, RED, NO_PERMS);
            return false;
        }
    }

    public void wrongUsage(Command command, Player player, Usage usage) {
        if (usage.equals(Usage.TO_MANY_ARGS)) {
            Message.getMessenger().msg(player, RED, "To many arguments!", command.getCorrectUsage(command));
        } else {
            Message.getMessenger().msg(player, RED, "Not enough arguments!", command.getCorrectUsage(command));
        }
    }

    public String getHelpTitle(boolean playerMenu) {
        String title = "Paintball";
        if (!playerMenu) {
            title += " Admin";
        }
        return Settings.getSettings().getSecondaryColor() + STRIKETHROUGH + "                    " + RESET + Settings.getSettings().getTheme() + " " + title + " " + Settings.getSettings().getSecondaryColor() + STRIKETHROUGH + "                    ";
    }

    public enum Usage {
        TO_MANY_ARGS,
        NOT_ENOUGH_ARGS;
    }
}