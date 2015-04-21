package me.synapz.paintball.commands.admin;


import me.synapz.paintball.Message;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetSpawn extends Command {

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


        arena.setArenaSpawn(spawn, team);
        Message.getMessenger().msg(player, ChatColor.GREEN, team + " spawn for " + arena.getName() + " set!", arena.getSteps());
    }

    public String getArgs() {
        String args = "<name> <red/blue>";
        return args;
    }

    public String getPermission() {
        return "paintball.admin.setspawn";
    }

    public String getName() {
        return "setspawn";
    }

    public String getInfo() {
        return "Set spawn of a Paintball Arena";
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
