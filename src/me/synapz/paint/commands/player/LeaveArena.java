package me.synapz.paint.commands.player;


import me.synapz.paint.commands.Command;
import org.bukkit.entity.Player;

public class LeaveArena extends Command {

    public void onCommand(Player player, String[] args) {

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

    public int getArgsInt() {
        return 1;
    }
}
