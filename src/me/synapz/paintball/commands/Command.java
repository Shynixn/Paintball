package me.synapz.paintball.commands;


import me.synapz.paintball.Message;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import org.bukkit.ChatColor;
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


    public enum CommandType {
        ADMIN,
        PLAYER;
    }

    public boolean nullCheck(String arenaName, Arena arena, Player sender) {
        if (arena == null) {
            Message.getMessenger().msg(sender, ChatColor.RED, arenaName + " is an invalid arena.");
            return false;
        } else {
            return true;
        }
    }

    public boolean teamCheck(String teamString, Player sender) {
        ArenaManager.Team team = stringToTeam(teamString);

        if (team == null) {
            Message.getMessenger().msg(sender, ChatColor.RED, teamString + " is an invalid team. Choose either red/blue");
            return false;
        } else {
            return true;
        }
    }

    public ArenaManager.Team stringToTeam(String teamString) {
        ArenaManager.Team team = null;
        if (teamString.equalsIgnoreCase("blue") || teamString.equalsIgnoreCase("red")) {
            team = teamString.equalsIgnoreCase("blue") ? ArenaManager.Team.BLUE : ArenaManager.Team.RED;
        }
        return team;
    }
}
