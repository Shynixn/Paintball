package me.synapz.paintball.commands.player;


import me.synapz.paintball.*;
import me.synapz.paintball.commands.TeamCommand;
import me.synapz.paintball.enums.CommandType;

import static org.bukkit.ChatColor.RED;

public class Join extends TeamCommand {

    public void onCommand() {
        // If the player types in /pb join
        if (arena == null) {
            Arena arena = ArenaManager.getArenaManager().getBestArena();
            if (arena == null) {
                Messenger.error(player, "No arenas are currently opened.");
                return;
            }
            arena.joinLobby(player, null);
            return;
        }
        //Joins the arena
        arena.joinLobby(player, team);
    }

    public String getArgs() {
        String args = "[arena] [team]";
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
        return 1;
    }

    protected int getTeamArg() {
        return 2;
    }

    protected int getArenaArg() {
        return 1;
    }
}
