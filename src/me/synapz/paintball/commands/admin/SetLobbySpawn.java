package me.synapz.paintball.commands.admin;


import me.synapz.paintball.*;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetLobbySpawn extends Command{

    public void onCommand(Player player, String[] args) {
        Location spawn = player.getLocation();
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);
        String teamString = args[3];
        Team team;

        if (Utils.nullCheck(args[2], arena, player) && teamCheck(arena, teamString, player)) {
            team = Utils.stringToTeam(arena, teamString);
            arena.setLobbySpawn(spawn, team);
            Message.getMessenger().msg(player, ChatColor.GREEN, team.getChatColor() + team.getTitleName() + ChatColor.GREEN + " lobby spawn for " + arena.toString() + " set!", arena.getSteps());
        } else {
            return;
        }
    }

    public String getArgs() {
        String args = "<arena> <team>";
        return args;
    }

    public String getPermission() {
        return "paintball.admin.setlobby";
    }

    public String getName() {
        return "setlobby";
    }

    public String getInfo() {
        return "Set lobby spawn";
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
