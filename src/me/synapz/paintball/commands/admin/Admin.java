package me.synapz.paintball.commands.admin;


import me.synapz.paintball.commands.Command;
import me.synapz.paintball.commands.CommandManager;
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
        return "paintball.admin.help";
    }

    public String getName() {
        return "admin";
    }

    public String getInfo() {
        return "Display all Paintball Admin commands";
    }

    public Command.CommandType getCommandType() {
        return type;
    }

    public int getMaxArgs() {
        return 1;
    }

    public int getMinArgs() {
        return 1;
    }

}
