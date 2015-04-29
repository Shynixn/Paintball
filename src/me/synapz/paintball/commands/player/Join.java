package me.synapz.paintball.commands.player;


import me.synapz.paintball.Message;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Join extends Command {

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[1]);
        ArenaManager.Team team = null;

        if (args.length == 3 && teamCheck(args[2], player)) {
            team = stringToTeam(args[2]);
        }
        if (arena == null) {
            Message.getMessenger().msg(player, ChatColor.RED, "Invalid arena.");
            return;
        }

        if (arena.containsPlayer(player)) {
            Message.getMessenger().msg(player, ChatColor.RED, "You are already in " + arena.toString() + ".");
            return;
        }
        arena.joinLobby(player, team);
    }

    public String getArgs() {
        String args = "<arena> [red/blue]";
        return args;
    }

    public String getPermission() {
        return "paintball.join";
    }

    public String getName() {
        return "join";
    }

    public String getInfo() {
        return "Join an Arena";
    }

    public CommandType getCommandType() {
        return CommandType.PLAYER;
    }

    public int getMaxArgs() {
        return 3;
    }

    public int getMinArgs() {
        return 2;
    }
}
