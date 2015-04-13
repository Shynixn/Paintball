package me.synapz.paint.commands.admin;


import me.synapz.paint.Message;
import me.synapz.paint.commands.Command;
import me.synapz.paint.commands.CommandManager;
import org.bukkit.entity.Player;

public class Admin extends Command{

    private Command.CommandType type = CommandType.PLAYER;

    public Admin(Command.CommandType t) {
        this.type = t;
    }

    public void onCommand(Player player, String[] args) {
        CommandManager.displayHelp(player, CommandType.ADMIN);
    }

    public String getArgs() {
        String args = "";
        return args;
    }

    public String getPermission() {
        return "paintball.admin";
    }

    public String getName() {
        return "admin";
    }

    public String getInfo() {
        return "Display Paintball Admin commands";
    }

    public Command.CommandType getCommandType() {
        return type;
    }

    public int[] getHandledArgs() {
        return new int[] {1};
    }

}
