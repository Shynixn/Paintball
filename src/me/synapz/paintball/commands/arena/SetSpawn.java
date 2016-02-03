package me.synapz.paintball.commands.arena;


import me.synapz.paintball.*;
import me.synapz.paintball.commands.TeamCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.locations.TeamLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class SetSpawn extends TeamCommand {

    public void onCommand() {
        Location spawn = player.getLocation();
        arena.setLocation(TeamLocation.TeamLocations.SPAWN, spawn, team);
        Message.getMessenger().msg(player, false, ChatColor.GREEN, team.getChatColor() + team.getTitleName() + ChatColor.GREEN + " spawn for " + arena.toString() + " set!", arena.getSteps());
    }

    public String getArgs() {
        String args = "<arena> <team>";
        return args;
    }

    public String getPermission() {
        return "paintball.arena.setspawn";
    }

    public String getName() {
        return "setspawn";
    }

    public String getInfo() {
        return "Set a team spawnpoint";
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

    protected int getArenaArg() {
        return 2;
    }

    protected int getTeamArg() {
        return 3;
    }
}
