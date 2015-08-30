package me.synapz.paintball.commands;


import me.synapz.paintball.Message;
import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Team;
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
        return "Usage: /paintball " + type + name + " " + command.getArgs();
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

    public boolean teamCheck(Arena a, String teamString, Player sender) {
        String validTeams = "";
        for (Team team : a.getArenaTeamList()) {
            validTeams += ChatColor.stripColor(team.getTitleName().toLowerCase() + " ");
        }
        // remove last space and replace spaces with /. So it should be <red/blue/green>
        validTeams = validTeams.substring(0, validTeams.lastIndexOf(" "));
        validTeams = validTeams.replaceAll(" ", "/");

        if (!validTeams.contains(teamString)) {
            Message.getMessenger().msg(sender, ChatColor.RED, teamString + " is an invalid team. Choose either <" + validTeams + ">");
            return false;
        } else {
            return true;
        }
    }
}
