package me.synapz.paintball.commands.arena;

import me.synapz.paintball.arenas.FlagArena;
import me.synapz.paintball.arenas.RTFArena;
import me.synapz.paintball.commands.TeamCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.Location;

public class SetFlag extends TeamCommand {

    // /pb arena setflag <arena> <team>

    public void onCommand() {
        Location flagLoc = player.getLocation();

        if (arena instanceof FlagArena) {
            if (team == null && arena instanceof RTFArena) {
                ((RTFArena) arena).setNuetralFlagLocation(flagLoc);
                Messenger.success(player, "Set " + arena.getName() + "'s " + " neutral flag to your location!", arena.getSteps());
            } else {
                ((FlagArena) arena).setFlagLocation(team, flagLoc);
                Messenger.success(player, "Set " + arena.getName() + "'s " + team.getTitleName() + " Team flag to your location!", arena.getSteps());
            }
        } else {
            Messenger.error(player, "That arena does not need any flags set!");
            return;
        }
    }

    public String getArgs() {
        String args = "<arena> <team>";
        return args;
    }

    public String getPermission() {
        return "paintball.arena.setflag";
    }

    public String getName() {
        return "setflag";
    }

    public Messages getInfo() {
        return Messages.COMMAND_SETFLAG_INFO;
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