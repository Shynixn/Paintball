package me.synapz.paint.commands.player;


import me.synapz.paint.Message;
import me.synapz.paint.arenas.Arena;
import me.synapz.paint.arenas.ArenaManager;
import me.synapz.paint.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Join extends Command {

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[1]);
        ArenaManager.Team team = null;

        if (args.length == 3) {
            team = stringToTeam(player, args[2]);
        }
        if (arena == null) {
            Message.getMessenger().msg(player, ChatColor.RED, "Invalid arena.");
            return;
        }

        if (arena.containsPlayer(player)) {
            Message.getMessenger().msg(player, ChatColor.RED, "You already joined that arena.");
            return;
        }

        if (arena.isSetup()) {
            arena.joinArena(player, team);
            // sendMessage, p.getname + joined 1/max players!
        } else {
            Message.getMessenger().msg(player, ChatColor.RED, "That arena has not been fully setup.");
        }
    }

    public String getArgs() {
        String args = "<name> [red/blue]";
        return args;
    }

    public String getPermission() {
        return "paintball.join";
    }

    public String getName() {
        return "join";
    }

    public String getInfo() {
        return "Join a Paintball Arena";
    }

    public CommandType getCommandType() {
        return CommandType.PLAYER;
    }

    public int[] getHandledArgs() {
        return new int[] {2, 3};
    }
}
