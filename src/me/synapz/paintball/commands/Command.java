package me.synapz.paintball.commands;


import me.synapz.paintball.arenas.ArenaManager;
import org.bukkit.entity.Player;

public abstract class Command {

    public abstract void onCommand(Player player, String[] args);

    public abstract String getName();

    public abstract String getInfo();

    public abstract String getArgs();

    public abstract String getPermission();

    public abstract CommandType getCommandType();

    public abstract int getMaxArgs();

    public abstract int getMinArgs();



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

    // TODO: use private methods instead of doing the same thing multiple times in classes that extend command

}
