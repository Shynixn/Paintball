package me.synapz.paint.commands.admin;

import me.synapz.paint.Message;
import me.synapz.paint.arenas.Arena;
import me.synapz.paint.arenas.ArenaManager;
import me.synapz.paint.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetMin extends Command{

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);
        String minString = args[3];
        int min = 0;

        if (arena == null) {
            Message.getMessenger().msg(player, ChatColor.RED, args[2] + " is an invalid arena.");
            return;
        }

        try {
            min = Integer.parseInt(minString);
        } catch (NumberFormatException e) {
            Message.getMessenger().msg(player, ChatColor.RED, minString + " is not a valid number!");
            return;
        }

        arena.setMinPlayers(min);
        Message.getMessenger().msg(player, ChatColor.GREEN, "Min players for " + arena.getName() + " set to " + min, "Steps: " + arena.getSteps());
    }

    public String getName() {
        return "min";
    }

    public String getInfo() {
        return "Set the minimum amount of players";
    }

    public String getArgs() {
        return "<name> <int>";
    }

    public String getPermission() {
        return "paintball.admin.setmin";
    }

    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    public int[] getHandledArgs() {
        return new int[] {4};
    }
}
