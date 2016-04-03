package me.synapz.paintball.commands.arena;

import me.synapz.paintball.arenas.CTFArena;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.commands.TeamCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.locations.FlagLocation;
import org.bukkit.Location;

public class DelFlag extends TeamCommand {

    // /pb arena delflag <arena> <team>

    public void onCommand() {
        Location flagLoc = player.getLocation();

        if (arena instanceof CTFArena) {
            new FlagLocation((CTFArena) arena, team, flagLoc).removeLocation();
            Messenger.success(player, "Deleted " + arena.getName() + "'s " + team.getTitleName() + " Team flag location!", arena.getSteps());
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
        return "paintball.arena.delflag";
    }

    public String getName() {
        return "delflag";
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
