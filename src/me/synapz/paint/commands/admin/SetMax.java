package me.synapz.paint.commands.admin;

import me.synapz.paint.Message;
import me.synapz.paint.arenas.Arena;
import me.synapz.paint.arenas.ArenaManager;
import me.synapz.paint.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetMax extends Command{

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);
        String maxString = args[3];
        int max = 0;

        if (arena == null) {
            Message.getMessenger().msg(player, ChatColor.RED, args[2] + " is an invalid arena.");
            return;
        }

        try {
            max = Integer.parseInt(maxString);
        } catch (NumberFormatException e) {
            Message.getMessenger().msg(player, ChatColor.RED, maxString + " is not a valid number!");
            return;
        }

        arena.setMaxPlayers(max);
        Message.getMessenger().msg(player, ChatColor.GREEN, "Max players for " + arena.getName() + " set to " + max, "Steps: " + arena.getSteps());
    }

    public String getName() {
        return "max";
    }

    public String getInfo() {
        return "Set the maximum amount of players";
    }

    public String getArgs() {
        return "<name> <int>";
    }

    public String getPermission() {
        return "paintball.admin.setmax";
    }

    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    public int getMaxArgs() {
        return 4;
    }
}
