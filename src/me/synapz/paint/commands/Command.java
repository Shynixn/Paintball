package me.synapz.paint.commands;


import me.synapz.paint.arenas.ArenaManager;
import org.bukkit.entity.Player;

public abstract class Command {

    public abstract void onCommand(Player player, String[] args);

    public abstract String getName();

    public abstract String getInfo();

    public abstract String getArgs();

    public abstract String getPermission();

    public abstract CommandType getCommandType();

    public abstract int[] getHandledArgs();



    public String getCorrectUsage(Command command) {
        String type = command.getCommandType() == CommandType.ADMIN ? "admin " : "";
        String name = command.getName().equals("admin") ? "" : command.getName();
        return "/paintball " + type + name + " " + command.getArgs();
    }

    public ArenaManager.Team stringToTeam(Player player, String t) {
        ArenaManager.Team team = null;
        if (t.equalsIgnoreCase("blue") || t.equalsIgnoreCase("red")) {
            team = t.equalsIgnoreCase("blue") ? ArenaManager.Team.BLUE : ArenaManager.Team.RED;
        }
        return team;
    }

    public enum CommandType {
        ADMIN,
        PLAYER;
    }

}
