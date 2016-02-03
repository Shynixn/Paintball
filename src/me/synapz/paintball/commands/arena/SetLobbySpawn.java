package me.synapz.paintball.commands.arena;


import me.synapz.paintball.*;
import me.synapz.paintball.commands.TeamCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.locations.TeamLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class SetLobbySpawn extends TeamCommand {

    public void onCommand() {
        Location spawn = player.getLocation();

        if (args[3].equalsIgnoreCase("all")) {
            for (Team t : arena.getArenaTeamList()) {
                arena.setLocation(TeamLocation.TeamLocations.LOBBY, spawn, t);
            }
            Message.getMessenger().msg(player, false, ChatColor.GREEN, "Set " + arena.getName() + "'s lobby spawns set to your location.", arena.getSteps());
            return;
        }
        arena.setLocation(TeamLocation.TeamLocations.LOBBY, spawn, team);
        Message.getMessenger().msg(player, false, ChatColor.GREEN, team.getChatColor() + team.getTitleName() + ChatColor.GREEN + " lobby spawn for " + arena.toString() + " set!", arena.getSteps());
    }

    public String getArgs() {
        String args = "<arena> <team/all>";
        return args;
    }

    public String getPermission() {
        return "paintball.arena.setlobby";
    }

    public String getName() {
        return "setlobby";
    }

    public String getInfo() {
        return "Set lobby spawn";
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
