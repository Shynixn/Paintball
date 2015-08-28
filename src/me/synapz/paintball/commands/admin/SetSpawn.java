package me.synapz.paintball.commands.admin;


import me.synapz.paintball.*;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetSpawn extends Command {

    public void onCommand(Player player, String[] args) {
        Location spawn = player.getLocation();
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);
        String teamString = args[3];
        Team team = stringToTeam(teamString);

        if (arena == null) {
            Message.getMessenger().msg(player, ChatColor.RED, args[2] + " is an invalid arena.");
            return;
        }

        if (teamCheck(teamString, player)) {
            arena.setArenaSpawn(spawn, team);
            Message.getMessenger().msg(player, ChatColor.GREEN, team.getTitleName() + " spawn for " + arena.toString() + " set!", arena.getSteps());
        }
    }

    public String getArgs() {
        String args = "<arena> <red/blue>";
        return args;
    }

    public String getPermission() {
        return "paintball.admin.setspawn";
    }

    public String getName() {
        return "setspawn";
    }

    public String getInfo() {
        return "Set spawnpoint";
    }

    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    public int getMaxArgs() {
        return 4;
    }

    public int getMinArgs() {
        return 4;
    }
}
