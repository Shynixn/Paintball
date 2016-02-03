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
        switch (arena.getState()) {
            case IN_PROGRESS:
                Message.getMessenger().msg(player, false, ChatColor.RED, arena.toString() + ChatColor.RED + " is currently in progress.");
                return;
            case STARTING:
                Message.getMessenger().msg(player, false, ChatColor.RED, arena.toString() + ChatColor.RED + " is currently in progress.");
                return;
            case NOT_SETUP:
                Message.getMessenger().msg(player, false, ChatColor.RED, arena.toString() + ChatColor.RED + " has not been fully setup.");
                return;
            case DISABLED:
                Message.getMessenger().msg(player, false, ChatColor.RED, arena.toString() + ChatColor.RED + " is disabled.");
                return;
            default:
                break;
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
