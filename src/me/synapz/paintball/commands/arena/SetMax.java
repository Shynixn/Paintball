package me.synapz.paintball.commands.arena;

import me.synapz.paintball.Message;
import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetMax extends Command{

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);
        String maxString = args[3];
        int max;

        if (arena == null) {
            Message.getMessenger().msg(player, false, ChatColor.RED, args[2] + " is an invalid arena.");
            return;
        }

        try {
            max = Integer.parseInt(maxString);
        } catch (NumberFormatException e) {
            Message.getMessenger().msg(player, false, ChatColor.RED, maxString + " is not a valid number!");
            return;
        }

        arena.setMaxPlayers(max);
        Message.getMessenger().msg(player, false, ChatColor.GREEN, "Max players for " + arena.toString() + " set to " + ChatColor.GRAY + max, arena.getSteps());
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
