package me.synapz.paintball.commands.player;


import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.commands.TeamCommand;
import me.synapz.paintball.enums.CommandType;

public class Join extends TeamCommand {

    public void onCommand() {
        // If the player types in /pb join
        if (args.length == 1) {
            Arena arena = ArenaManager.getArenaManager().getBestArena();
            if (arena == null) {
                Messenger.error(player, "No arenas are currently opened.");
                return;
            } else {
                arena.joinLobby(player, null);
            }
        } else if (args.length == 2) {
            // If the player types in /pb join Arena
            arena.joinLobby(player, null);
        } else if (args.length == 3) {
            // If the player types in /pb join Arena Team
            arena.joinLobby(player, team);
        }
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
