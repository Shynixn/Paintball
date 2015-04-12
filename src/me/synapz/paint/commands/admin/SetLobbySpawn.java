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

        if (arena == null) {
            Message.getMessenger().msg(player, ChatColor.RED, "Invalid arena.");
            return;
        }

        if (arena.getLobbySpawn() != null) {
            Message.getMessenger().msg(player, ChatColor.RED, "Arena lobby is already set!");
            return;
        }

        arena.setLobbySpawn(spawn);
        Message.getMessenger().msg(player, ChatColor.GREEN, "Lobby spawn for " + arena.getName() + " set!");
    }

    public String getArgs() {
        String args = "<name>";
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
        return 3;
    }

}
