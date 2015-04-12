package me.synapz.paint.commands;


import org.bukkit.entity.Player;

public abstract class Command {

    public abstract void onCommand(Player player, String[] args);

    public abstract String getName();

    public abstract String getInfo();

    public abstract String getArgs();

    public abstract String getPermission();

    public abstract CommandType getCommandType();

    public abstract int getArgsInt();

    public String getCorrectUsage(Command command) {
        String type = command.getCommandType() == CommandType.ADMIN ? "admin " : "";
        return "/paintball " + type + command.getName() + " " + command.getArgs();
    }

    // check config for next steps and strike them out...

    public enum CommandType {
        ADMIN,
        PLAYER;
    }

}
