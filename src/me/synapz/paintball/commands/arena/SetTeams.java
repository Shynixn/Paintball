package me.synapz.paintball.commands.arena;

import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class SetTeams extends ArenaCommand {

    public void onCommand() {
        ArrayList<Team> teamsToAdd = new ArrayList<Team>();

        List<String> colors = Arrays.asList(args[3].split(","));
        // used to make sure a team isn't added two times
        List<String> added = new ArrayList<String>();
            // The amount of teams have to be greater than or equal to the max, this will only check if min was never set
        if (colors.size() > arena.getMax() && arena.getMax() != 0) {
            Messenger.error(player, "The amount of teams (" + GRAY + colors.size()+ RED + ") must be greater than or equal to the max amount of players (" + GRAY + arena.getMax() + RED + ")!");
            return;
        }
        if (colors.size() == 1) {
            Messenger.error(player, arena.toString(RED) + " cannot have only one team!");
            return;
        }
        for (String color : colors) {
            if (!Team.DEFAULT_NAMES.containsKey(color)) {
                Messenger.error(player, "Error parsing ChatColors. For example use,", "Usage: /pb admin setteam " + arena.getName() + " &1,&2,&6,&a");
                return;
            }
            color = translateAlternateColorCodes('&', color);
            // make sure no teams are duplicated
            if (added.contains(color)) {
                if (colors.size() == 2) {
                    // when two of the same teams are added as &1,&1
                    Messenger.error(player, "Cannot have two of the same color.");
                    return;
                }
                // get out of this iteration of the loop this way the duplicated team doesn't get added
                continue;
            }
            teamsToAdd.add(new Team(arena, color));
            added.add(color);
        }
        arena.setArenaTeamList(teamsToAdd);
        // generate the message to be send back to the sender
        String out = "";
        for (Team t : arena.getArenaTeamList()) {
            out += t.getTitleName() + ", ";
        }
        out = out.substring(0, out.lastIndexOf(","));
        Messenger.success(player, arena.toString(ChatColor.GREEN) + "'s teams has been set to " + out + "!", arena.getSteps());
    }

    public String getArgs() {
        String args = "<arena> <chatcolors...>";
        return args;
    }

    public String getPermission() {
        return "paintball.arena.team";
    }

    public String getName() {
        return "setteams";
    }

    public Messages getInfo() {
        return Messages.COMMAND_SETTEAMS_INFO;
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

    public int getArenaArg() {
        return 2;
    }
}
