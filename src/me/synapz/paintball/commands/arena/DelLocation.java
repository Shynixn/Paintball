package me.synapz.paintball.commands.arena;

import me.synapz.paintball.Message;
import me.synapz.paintball.Team;
import me.synapz.paintball.commands.TeamCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class DelLocation extends TeamCommand {

    public void onCommand() {
        TeamLocation.TeamLocations type = stringToLocationType(args[3]);

        if (type == null) {
            Message.getMessenger().msg(player, false, ChatColor.RED, "Value " + args[3] + " is not a valid type.", "Choose either <spawn/lobby>");
            return;
        }

        if (args[4].equalsIgnoreCase("all")) {
            for (Team t : arena.getArenaTeamList()) {
                int size = t.getSpawnPointsSize(type);
                if (size != 0) {
                    while (size > 0) {
                        arena.delLocation(type, t, size);
                        size--;
                    }
                }
            }
            Message.getMessenger().msg(player, false, ChatColor.GREEN, "Deleted all " + arena.getName() + "'s " + (type.toString().toLowerCase().equals("spawn") ? "arena" : "lobby") + " spawns.", arena.getSteps());
            return;
        }

        if (team.getSpawnPointsSize(type) == 0) {
            Message.getMessenger().msg(player, false, ChatColor.RED, "There are no more " + type.toString().toLowerCase() + " spawns to be deleted.");
            return;
        }

        arena.delLocation(type, team, team.getSpawnPointsSize(type));
        Message.getMessenger().msg(player, false, ChatColor.GREEN, "Deleted " + arena.getName() + "'s " + type.toString().toLowerCase() + " spawn to your location: " + Settings.SECONDARY + team.getSpawnPointsSize(type), arena.getSteps());
    }

    public String getArgs() {
        String args = "<arena> <spawn/lobby> <team/all>";
        return args;
    }

    public String getPermission() {
        return "paintball.arena.delteamlocation";
    }

    public String getName() {
        return "del";
    }

    public String getInfo() {
        return "Delete arena locations";
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
