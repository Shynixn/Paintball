package me.synapz.paint.arenas;


import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Arena {

    private int max;
    private int min;
    private String name;
    private Location redspawn, bluespawn;
    private HashMap<String, ArenaManager.Team> players = new HashMap<String, ArenaManager.Team>();

    public Arena(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Location getSpawn(ArenaManager.Team team) {
        switch (team) {
            case RED:
                return redspawn;
            case BLUE:
                return bluespawn;
            default:
                return null;
        }
    }

    public void setSpawn(Location location, ArenaManager.Team team) {
        switch (team) {
            case RED:
                redspawn = location;
                break;
            case BLUE:
                bluespawn = location;
                break;
        }
    }

    public void setMaxPlayers(int max) {
        this.max = max;
    }

    public void setMinPlayers(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public ArenaManager.Team getTeam(Player player) {
        return players.get(player.getName());
    }

    public void addPlayer(Player player) {
        players.put(player.getName(), getTeamWithLessPlayers());
    }

    public boolean containsPlayer(Player player) {
        return players.keySet().contains(player.getName());
    }

    private ArenaManager.Team getTeamWithLessPlayers() {
        int red = 0, blue = 0;
        for (String p : players.keySet()) {
            if (players.get(p) == ArenaManager.Team.RED)
                red++;
            else
                blue++;
        }
        if (red > blue)
            return ArenaManager.Team.BLUE;
        else
            return ArenaManager.Team.RED;
    }


}
