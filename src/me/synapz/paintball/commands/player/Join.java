package me.synapz.paintball.commands.player;


import me.synapz.paintball.*;
import me.synapz.paintball.commands.TeamCommand;
import me.synapz.paintball.enums.CommandType;
import org.bukkit.ChatColor;

import static org.bukkit.ChatColor.RED;

public class Join extends TeamCommand {

    public void onCommand() {
        for (Arena a : ArenaManager.getArenaManager().getArenas().values()) {
            if (a.containsPlayer(player)) {
                Message.getMessenger().msg(player, false, ChatColor.RED, "You are already in " + a.toString() + ChatColor.RED + ".");
                return;
            }
        }
        if (arena.getLobbyPlayers().size() == arena.getMax() && arena.getMax() > 0) {
            Message.getMessenger().msg(player, false, RED, arena.toString() + RED + " is full!");
            return;
        }

        arena.joinLobby(player, team);
    }

    public String getArgs() {
        String args = "<arena> [team]";
        return args;
    }

    public String getPermission() {
        return "paintball.join";
    }

    public String getName() {
        return "join";
    }

    public String getInfo() {
        return "Join an Arena";
    }

    public CommandType getCommandType() {
        return CommandType.PLAYER;
    }

    public int getMaxArgs() {
        return 3;
    }

    public int getMinArgs() {
        return 2;
    }

    protected int getTeamArg() {
        return 2;
    }

    protected int getArenaArg() {
        return 1;
    }
}
