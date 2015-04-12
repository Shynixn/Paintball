package me.synapz.paint.commands.player;


import me.synapz.paint.commands.Command;
import org.bukkit.entity.Player;

public class Join extends Command {

    public void onCommand(Player player, String[] args) {

    }

    public String getArgs() {
        String args = "<id>";
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

    public int getArgsInt() {
        return 2;
    }
}
