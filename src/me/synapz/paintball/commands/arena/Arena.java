package me.synapz.paintball.commands.arena;

import me.synapz.paintball.commands.Command;
import me.synapz.paintball.commands.CommandManager;
import org.bukkit.entity.Player;

public class Arena extends Command {

    private Command.CommandType type = CommandType.ARENA;

    public Arena(Command.CommandType t) {
        this.type = t;
    }

    public void onCommand(Player player, String[] args) {
        CommandManager.displayHelp(player, CommandType.ARENA);
    }

    public String getArgs() {
        String args = "";
        return args;
    }

    public String getPermission() {
        return "paintball.arena.help";
    }

    public String getName() {
        return "arena";
    }

    public String getInfo() {
        return "Display all Paintball Arena setup commands";
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
