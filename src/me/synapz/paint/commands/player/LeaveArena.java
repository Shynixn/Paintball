package me.synapz.paint.commands.player;


import me.synapz.paint.Message;
import me.synapz.paint.arenas.Arena;
import me.synapz.paint.arenas.ArenaManager;
import me.synapz.paint.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LeaveArena extends Command {

    public void onCommand(Player player, String[] args) {
        Arena a = null;
        try {
            a = ArenaManager.getArenaManager().getArena(player);
            a.getName(); // used to see if it returns null
        }catch (NullPointerException e) {
            Message.getMessenger().msg(player, ChatColor.RED, "You are not in an arena.");
            return;
        }

        a.removePlayer(player);
        Message.getMessenger().msg(player, ChatColor.GREEN, "Successfully left arena.");


    }

    public String getArgs() {
        String args = "";
        return args;
    }

    public String getPermission() {
        return "paintball.leave";
    }

    public String getName() {
        return "leave";
    }

    public String getInfo() {
        return "Leave a Paintball Arena";
    }

    public CommandType getCommandType() {
        return CommandType.PLAYER;
    }

    public int getMaxArgs() {
        return 1;
    }

    public int getMinArgs() {
        return 1;
    }
}
