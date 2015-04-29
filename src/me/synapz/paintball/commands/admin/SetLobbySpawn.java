package me.synapz.paintball.commands.admin;


import me.synapz.paintball.Message;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetLobbySpawn extends Command{

    public void onCommand(Player player, String[] args) {
        Location spawn = player.getLocation();
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);
        String teamString = args[3];
        ArenaManager.Team team;

        if (nullCheck(args[2], arena, player) && teamCheck(teamString, player)) {
            team = stringToTeam(teamString);
            arena.setLobbySpawn(spawn, team);
            Message.getMessenger().msg(player, ChatColor.GREEN, team + " lobby spawn for " + arena.toString() + " set!", arena.getSteps());
        } else {
            return;
        }
    }

    public String getArgs() {
        String args = "<arena> <red/blue>";
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
