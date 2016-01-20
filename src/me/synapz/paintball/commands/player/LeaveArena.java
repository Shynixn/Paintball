package me.synapz.paintball.commands.player;


import me.synapz.paintball.Message;
import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LeaveArena extends Command {

    public void onCommand(Player player, String[] args) {
        Arena a;
        try {
            a = ArenaManager.getArenaManager().getArena(player);
            a.getName(); // used to see if it returns null
        }catch (NullPointerException e) {
            Message.getMessenger().msg(player, false, ChatColor.RED, "You are not in an arena.");
            return;
        }

        a.getAllPlayers().get(player).leaveArena();
        Message.getMessenger().msg(player, true, ChatColor.GREEN, "Successfully left arena.");
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
        return "Leave an Arena";
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
