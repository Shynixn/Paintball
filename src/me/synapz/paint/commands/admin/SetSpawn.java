package me.synapz.paint.commands.admin;


import me.synapz.paint.commands.Command;
import org.bukkit.entity.Player;

public class SetSpawn extends Command {

    public void onCommand(Player player, String[] args) {

    }

    public String getArgs() {
        String args = "<id>";
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
        return 3;
    }
}
