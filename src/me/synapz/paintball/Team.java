package me.synapz.paintball;

import org.bukkit.ChatColor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public enum Team {

    Team1("Teams.team1", "Team1"),
    Team2("Teams.team2", "Team2"),
    Team3("Teams.team3", "Team3"),
    Team4("Teams.team4", "Team4");

    private String color, path, name;
    private boolean isSet;

    Team (String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setChatColor(String color) {
        this.color = color;
    }

    public String getChatColor() {
        return color;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setIsSet(boolean set) {
        this.isSet = set;
    }

    public static Team[] getEnabledTeams() {
        ArrayList<Team> teams = new ArrayList<Team>();
        for (Team team : values()) {
            if (team.isSet()) {
                teams.add(team);
            }
        }
        Team[] list = new Team[teams.size()];
        int i = 0;
        for (Team t : teams) {
            list[i] = t;
            i++;
        }
        return list;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getTitleName() {
        String color = "error";
        String[] list = new String[] {"§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§0", "§a", "§b", "§c", "§d", "§e", "§f"};
        String[] names = new String[] {"Blue", "Green", "Cyan", "Red", "Purple", "Yellow", "Gray", "Gray", "Blue", "Black", "Green", "Blue", "Red", "Purple", "Yellow"};
        int i = 0;
        for (String s : list) {
            if (s.toCharArray()[1] == getChatColor().toCharArray()[1]) {
                color = names[i];
                break;
            }
            i++;
        }
        return color;
    }
}
