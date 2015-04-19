package me.synapz.paint.commands.admin;


import me.synapz.paint.Message;
import me.synapz.paint.arenas.Arena;
import me.synapz.paint.arenas.ArenaManager;
import me.synapz.paint.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetLobbySpawn extends Command{

    public void onCommand(Player player, String[] args) {
        Location spawn = player.getLocation();
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);
        String teamString = args[3];
        ArenaManager.Team team;

        if (arena == null) {
            Message.getMessenger().msg(player, ChatColor.RED, args[2] + " is an invalid arena.");
            return;
        }

        if (teamString.equalsIgnoreCase("blue") || teamString.equalsIgnoreCase("red")) {
            team = teamString.equalsIgnoreCase("blue") ? ArenaManager.Team.BLUE : ArenaManager.Team.RED;
        } else {
            Message.getMessenger().msg(player, ChatColor.RED, args[3] + " is an invalid team. Choose either red/blue");
            return;
        }


        // see the class 'SetSpawn' for an explanation on this
        try {
            arena.getLobbySpawn(team);
            Message.getMessenger().msg(player, ChatColor.RED, "Lobby for " + team + " was already set.");
            return;
        }catch (ClassCastException e) {
            // continue, config cannot turn 'not_set' into a location, proving it isn't a location & was never set
        }

        arena.setLobbySpawn(spawn, team);
        Message.getMessenger().msg(player, ChatColor.GREEN, team + " lobby spawn for " + arena.getName() + " set!", arena.getSteps());
    }

    public String getArgs() {
        String args = "<name> <red/blue>";
        return args;
    }

    public String getPermission() {
        return "paintball.admin.setlobby";
    }

    public String getName() {
        return "setlobby";
    }

    public String getInfo() {
        return "Set lobby of a Paintball Arena";
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
