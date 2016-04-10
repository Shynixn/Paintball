package me.synapz.paintball.commands.arena;

import me.synapz.paintball.arenas.CTFArena;
import me.synapz.paintball.arenas.FlagArena;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.commands.TeamCommand;
import me.synapz.paintball.enums.CommandType;
import org.bukkit.Location;

public class SetFlag extends TeamCommand {

    // /pb arena setflag <arena> <team>

    public void onCommand() {
        Location flagLoc = player.getLocation();

        if (arena instanceof FlagArena) {
            ((FlagArena) arena).setFlagLocation(team, flagLoc);
            Messenger.success(player, "Set " + arena.getName() + "'s " + team.getTitleName() + " Team flag to your location!", arena.getSteps());
        } else {
            Messenger.error(player, "That arena is not a CTF Arena!");
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

    public String getInfo() {
        return "Set a CTF Arena's flag";
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
