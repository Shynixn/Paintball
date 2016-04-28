package me.synapz.paintball.commands.admin;

import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static me.synapz.paintball.storage.Settings.*;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.STRIKETHROUGH;

public class Info extends ArenaCommand {

    public void onCommand() {
        final String LINE = SECONDARY + STRIKETHROUGH + Utils.makeSpaces(20) + RESET + " " + THEME + arena.getName() + " " + SECONDARY + STRIKETHROUGH + Utils.makeSpaces(20);
        final int specSize = ARENA_FILE.getConfigurationSection(arena.getPath() + "Spectator") != null ? ARENA_FILE.getConfigurationSection(arena.getPath() + "Spectator").getValues(false).size() : 0;
        final List<String> teams = readableList(arena.getArenaTeamList());

        Messenger.msg(player,
                LINE,
                THEME + "State: " + SECONDARY + arena.getState(),
                THEME + "Type: " + SECONDARY + arena.getArenaType().getFullName(),
                THEME + "Min: " + SECONDARY + arena.getMin(),
                THEME + "Max: " + SECONDARY + arena.getMax(),
                THEME + "Enabled: " + SECONDARY + arena.isEnabled(),
                THEME + "Join Signs: " + SECONDARY + arena.getSignLocations().keySet().size(),
                THEME + "Spectator Locations: " + SECONDARY + specSize,
                THEME + "Teams: " + SECONDARY + (teams.isEmpty() ? "Empty" : ""));
        for (String item : teams)
            Messenger.msg(player, SECONDARY + item + "");

    }

    public String getArgs() {
        String args = "<arena>";
        return args;
    }

    public String getPermission() {
        return "paintball.admin.info";
    }

    public String getName() {
        return "info";
    }

    public Messages getInfo() {
        return Messages.COMMAND_INFO_INFO;
    }

    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    public int getMaxArgs() {
        return 3;
    }

    public int getMinArgs() {
        return 3;
    }

    protected int getArenaArg() {
        return 2;
    }

    private List<String> readableList(Set<Team> teams) {
        return new ArrayList<String>(){{
            for (Team team : teams) {
                add("  - " + team.getTitleName());
                add("      " + THEME + "Color: " + team.getChatColor() + "█");
                add("      " + THEME + "Lobby locations: " + SECONDARY + team.getSpawnPointsSize(TeamLocation.TeamLocations.LOBBY));
                add("      " + THEME + "Spawn locations: " + SECONDARY + team.getSpawnPointsSize(TeamLocation.TeamLocations.SPAWN));
            }
        }};
    }
}

