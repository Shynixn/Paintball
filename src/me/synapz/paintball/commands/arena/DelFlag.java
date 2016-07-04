package me.synapz.paintball.commands.arena;

import me.synapz.paintball.arenas.FlagArena;
import me.synapz.paintball.commands.TeamCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.locations.FlagLocation;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.Location;

public class DelFlag extends TeamCommand {

    // /pb arena delflag <arena> <team>

    public void onCommand() {
        Location flagLoc = player.getLocation();

        if (arena instanceof FlagArena) {
            new FlagLocation((FlagArena) arena, team, flagLoc).removeLocation();
            Messenger.success(player, "Deleted " + arena.getName() + "'s " + (team== null ? "neutral" : team.getTitleName()) + " Team flag location!", arena.getSteps());
        } else {
            Messenger.error(player, "That arena does not need any flags!");
            return;
        }
    }

    public String getArgs() {
        String args = "<arena> <team>";
        return args;
    }

    public String getPermission() {
        return "paintball.arena.delflag";
    }

    public String getName() {
        return "delflag";
    }

    public Messages getInfo() {
        return Messages.COMMAND_DELFLAG_INFO;
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

    protected int getTeamArg() {
        return 3;
    }

    protected int getArenaArg() {
        return 2;
    }
}
