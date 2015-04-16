package me.synapz.paint;


import me.synapz.paint.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message {

    public static final String SUFFIX = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "PaintBall" + ChatColor.DARK_GRAY + "] ";
    public static final String NO_PERMS = "You don't have access to that command!";

    private static Message instance = new Message();

    public static Message getMessenger() {
        return instance;
    }

    public void msg(CommandSender sender, ChatColor color, String... msg){
        String[] messages = msg;

        for (String string : messages) {
            sender.sendMessage(SUFFIX + color + string);
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

    public void wrongUsage(Command command, Player player) {
        msg(player, ChatColor.RED, command.getInfo(), "Usage: " + command.getCorrectUsage(command));
    }

    public void wrongUsage(Command command, Player player, Usage usage) {
        if (usage.equals(Usage.TO_MANY_ARGS)) {
            Message.getMessenger().msg(player, ChatColor.RED, "To many arguments!", "Usage: " + command.getCorrectUsage(command));
        } else {
            Message.getMessenger().msg(player, ChatColor.RED, "Not enough arguments!", "Usage: " + command.getCorrectUsage(command));
        }
    }

    public enum Usage {
        TO_MANY_ARGS,
        NOT_ENOUGH_ARGS;
    }


}
