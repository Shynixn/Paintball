package me.synapz.paintball.commands.arena;

import me.synapz.paintball.Message;
import me.synapz.paintball.Team;
import me.synapz.paintball.commands.TeamCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class SetLocation extends TeamCommand {

    // /pb arena set <spawn/lobby> [all]
    public void onCommand() {
        TeamLocation.TeamLocations type = stringToLocationType(args[3]);
        Location spawn = player.getLocation();

        if (type == null) {
            Message.getMessenger().msg(player, false, ChatColor.RED, "Value " + args[3] + " is not a valid type.", "Choose either <spawn/lobby>");
            return;
        }

        if (args[4].equalsIgnoreCase("all")) {
            for (Team t : arena.getArenaTeamList()) {
                arena.setLocation(type, spawn, t);
            }
            Message.getMessenger().msg(player, false, ChatColor.GREEN, "Set all " + arena.getName() + "'s " + (type.toString().toLowerCase().equals("spawn") ? "arena" : "lobby") + " spawns to your location." + Settings.SECONDARY, arena.getSteps());
            return;
        }
        arena.setLocation(type, spawn, team);
        Message.getMessenger().msg(player, false, ChatColor.GREEN, "Set " + arena.getName() + "'s " + type.toString().toLowerCase() + " spawn to your location: " + Settings.SECONDARY + team.getSpawnPointsSize(type), arena.getSteps());
    }

    public String getArgs() {
        String args = "<arena> <spawn/lobby> <team/all>";
        return args;
    }

    public String getPermission() {
        return "paintball.arena.setteamlocation";
    }

    public String getName() {
        return "set";
    }

    public String getInfo() {
        return "Set arena locations";
    }

    public CommandType getCommandType() {
        return CommandType.ARENA;
    }

    public int getMaxArgs() {
        return 5;
    }

    public int getMinArgs() {
        return 5;
    }

    protected int getTeamArg() {
        return 4;
    }

    protected int getArenaArg() {
        return 2;
    }

    private TeamLocation.TeamLocations stringToLocationType(String value) {
        for (TeamLocation.TeamLocations enumLocs : TeamLocation.TeamLocations.values()) {
            if (enumLocs.toString().equalsIgnoreCase(value))
                return enumLocs;
        }
        return null;
    }
}
