package me.synapz.paintball;


import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message {

    // config values
    public static final String PREFIX = Settings.getSettings().prefix == null ? ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "Paintball" + ChatColor.DARK_GRAY + "] " : Settings.getSettings().prefix + " ";
    public static final String THEME = Settings.getSettings().theme == null ? ChatColor.translateAlternateColorCodes('&', "&3") : Settings.getSettings().theme;

    // determined default values
    public static final String NO_PERMS = "You don't have access to that command!";

    private static Message instance = new Message();

    public static Message getMessenger() {
        return instance;
    }

    public void msg(CommandSender sender, ChatColor color, String... msg){
        String[] messages = msg;

        for (String string : messages) {
            sender.sendMessage(PREFIX + color + string);
        }
    }

    public boolean permissionValidator(Player player, String permission) {
        if (player.hasPermission(permission)) {
            return true;
        } else {
            msg(player, ChatColor.RED, NO_PERMS);
            return false;
        }
    }

    public void wrongUsage(Command command, Player player, Usage usage) {
        if (usage.equals(Usage.TO_MANY_ARGS)) {
            Message.getMessenger().msg(player, ChatColor.RED, "To many arguments!", command.getCorrectUsage(command));
        } else {
            Message.getMessenger().msg(player, ChatColor.RED, "Not enough arguments!", command.getCorrectUsage(command));
        }
    }

    public enum Usage {
        TO_MANY_ARGS,
        NOT_ENOUGH_ARGS;
    }


}
