package me.synapz.paintball.commands.player;


import me.synapz.paintball.Message;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.Command;
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