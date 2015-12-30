package me.synapz.paintball.commands.arena;


import me.synapz.paintball.*;
import me.synapz.paintball.Arena;
import me.synapz.paintball.commands.Command;
import me.synapz.paintball.locations.TeamLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetLobbySpawn extends Command{

    public void onCommand(Player player, String[] args) {
        Location spawn = player.getLocation();
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);
        String teamString = args[3];
        Team team;

        if (!Utils.nullCheck(args[2], arena, player)) return;

        if (arena.getArenaTeamList().isEmpty()) {
            Message.getMessenger().msg(player, false, ChatColor.RED, arena.toString() + ChatColor.RED + " does not have any teams set!");
            return;
        }

        if (args[3].equalsIgnoreCase("all")) {
            for (Team t : arena.getArenaTeamList()) {
                arena.setLocation(TeamLocation.TeamLocations.LOBBY, spawn, t);
            }
            Message.getMessenger().msg(player, false, ChatColor.GREEN, "Set " + arena.getName() + "'s lobby spawns set to your location.", arena.getSteps());
            return;
        }
        if (teamCheck(arena, teamString, player)) {
            team = Utils.stringToTeam(arena, teamString);
            arena.setLocation(TeamLocation.TeamLocations.LOBBY, spawn, team);
            Message.getMessenger().msg(player, false, ChatColor.GREEN, team.getChatColor() + team.getTitleName() + ChatColor.GREEN + " lobby spawn for " + arena.toString() + " set!", arena.getSteps());
        } else {
            return;
        }
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

}
