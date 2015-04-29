package me.synapz.paintball.commands.admin;

import me.synapz.paintball.Message;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetMin extends Command{

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);
        String minString = args[3];
        int min;

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
        Message.getMessenger().msg(player, ChatColor.GREEN, "Min players for " + arena.toString() + " set to " + ChatColor.GRAY + min, arena.getSteps());
    }

    public String getName() {
        return "min";
    }

    public String getInfo() {
        return "Set the min amount of players";
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

    public int getMaxArgs() {
        return 4;
    }

    public int getMinArgs() {
        return 4;
    }
}
