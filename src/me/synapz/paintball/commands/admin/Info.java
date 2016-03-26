package me.synapz.paintball.commands.admin;

import me.synapz.paintball.Messenger;
import me.synapz.paintball.Team;
import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static me.synapz.paintball.storage.Settings.*;

public class Info extends ArenaCommand {

    public void onCommand() {
        Messenger.msg(player,
                THEME + "State: " + SECONDARY + arena.getState(),
                THEME + "Min: " + SECONDARY + arena.getMin(),
                THEME + "Max: " + SECONDARY + arena.getMax(),
                THEME + "Teams: ");
        for (String item : readableList(arena.getArenaTeamList()))
            Messenger.msg(player, SECONDARY + item);
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

    public String getInfo() {
        return "Display Arena information.";
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
            for (Team team : teams)
                add("  - " + team.getTitleName());
        }};
    }
}

