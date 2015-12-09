package me.synapz.paintball.commands.arena;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Message;
import me.synapz.paintball.commands.Command;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

public class SetMax extends Command {

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);
        String maxString = args[3];
        int max;

        if (arena == null) {
            Message.getMessenger().msg(player, false, RED, args[2] + " is an invalid arena.");
            return;
        }

        try {
            max = Integer.parseInt(maxString);
        } catch (NumberFormatException e) {
            Message.getMessenger().msg(player, false, RED, maxString + " is not a valid number!");
            return;
        }

        if (arena.getMin() == 0 || max > arena.getMin()) {
            if (max <= 0) {
                Message.getMessenger().msg(player, false, RED, "Max must be greater than 0!");
                return;
            }
            arena.setMaxPlayers(max);
        } else {
            Message.getMessenger().msg(player, false, RED, "Your max (" + GRAY + max + RED + ") must be greater than your min (" + GRAY + arena.getMin() + RED + ") !");
            return;
        }
        Message.getMessenger().msg(player, false, GREEN, "Max players for " + arena.toString() + " set to " + GRAY + max, arena.getSteps());
    }

    public String getName() {
        return "max";
    }

    public String getInfo() {
        return "Set max number of players";
    }

    public String getArgs() {
        return "<arena> <number>";
    }

    public String getPermission() {
        return "paintball.arena.setmax";
    }

    public CommandType getCommandType() {
        return CommandType.ARENA;
    }

    public int getMaxArgs() {
        return 4;
    }

    public int getMinArgs() {
        return 4;
    }
}
