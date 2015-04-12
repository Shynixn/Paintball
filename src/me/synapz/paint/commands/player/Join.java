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

        if (arena == null) {
            Message.getMessenger().msg(player, ChatColor.RED, "Invalid arena.");
            return;
        }

        if (arena.containsPlayer(player)) {
            Message.getMessenger().msg(player, ChatColor.RED, "You already joined the arena!");
            return;
        }

        if (arena.isSetup()) {
            arena.addPlayer(player);
        }
    }

    public String getArgs() {
        String args = "<name>";
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

    public int getMaxArgs() {
        return 2;
    }
}
