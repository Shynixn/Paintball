package me.synapz.paintball.commands.arena;

import com.google.common.base.Joiner;
import me.synapz.paintball.*;
import me.synapz.paintball.Arena;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class SetTeams extends Command {

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);
        ArrayList<Team> teamsToAdd = new ArrayList<Team>();

        if (Utils.nullCheck(args[2], arena, player)) {
            List<String> colors = Arrays.asList(args[3].split(","));
            // used to make sure a team isn't added two times
            List<String> added = new ArrayList<String>();

            // The amount of teams have to be greater than or equal to the max, this will only check if min was never set
            if (colors.size() > arena.getMax() && arena.getMax() != 0) {
                Message.getMessenger().msg(player, false, RED, "The amount of teams (" + GRAY + colors.size()+ RED + ") must be greater than or equal to the max amount of players (" + GRAY + arena.getMax() + RED + ")!");
                return;
            }

            if (colors.size() == 1) {
                Message.getMessenger().msg(player, false, RED, arena.toString() + RED + " cannot have only one team!");
                return;
            }
            for (String color : colors) {
                color = translateAlternateColorCodes('&', color);
                if (!Team.LIST.contains(color)) {
                    Message.getMessenger().msg(player, false, RED, "Error parsing ChatColors. For example use,", "Usage: /pb admin setteam " + arena.getName() + " &1,&2,&6,&a");
                    return;
                }
                // make sure no teams are duplicated
                if (added.contains(color)) {
                    if (colors.size() == 2) {
                        // when two of the same teams are added as &1,&1
                        Message.getMessenger().msg(player, false, RED, "Cannot have two of the same color.");
                        return;
                    }
                    // get out of this iteration of the loop this way the duplicated team doesn't get added
                    continue;
                }
                teamsToAdd.add(new Team(arena, color));
                added.add(color);
            }
        } else {
            return;
        }
        arena.setArenaTeamList(teamsToAdd);
        // generate the message to be send back to the sender
        String out = "";
        for (Team t : arena.getArenaTeamList()) {
            out += t.getTitleName() + ", ";
        }
        out = out.substring(0, out.lastIndexOf(","));
        Message.getMessenger().msg(player, false, GREEN, arena.toString() + "'s teams has been set to " + out + "!", arena.getSteps());
    }

    public String getArgs() {
        String args = "<arena> <chatcolors...>";
        return args;
    }

    public String getPermission() {
        return "paintball.arena.setteam";
    }

    public String getName() {
        return "setteam";
    }

    public String getInfo() {
        return "Set teams via ChatColors seperated by commas ex. &1,&b,&c";
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

}
