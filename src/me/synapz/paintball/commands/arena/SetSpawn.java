package me.synapz.paintball.commands.arena;


import me.synapz.paintball.*;
import me.synapz.paintball.Arena;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetSpawn extends Command {

    public void onCommand(Player player, String[] args) {
        Location spawn = player.getLocation();
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);

        if (arena == null) {
            Message.getMessenger().msg(player, false, ChatColor.RED, args[2] + " is an invalid arena.");
            return;
        }
        Team team = Utils.stringToTeam(arena, args[3]);

        if (arena.getArenaTeamList().isEmpty()) {
            Message.getMessenger().msg(player, false, ChatColor.RED, arena.toString() + ChatColor.RED + " does not have any teams set!");
            return;
        }

        if (teamCheck(arena, args[3], player)) {
            arena.setArenaSpawn(spawn, team);
            Message.getMessenger().msg(player, false, ChatColor.GREEN, team.getChatColor() + team.getTitleName() + ChatColor.GREEN + " spawn for " + arena.toString() + " set!", arena.getSteps());
        }
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
}
