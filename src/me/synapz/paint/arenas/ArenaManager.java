package me.synapz.paint.arenas;


import me.synapz.paint.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ArenaManager {

    public enum Team {
        RED,
        BLUE;
    }
    private ArenaManager() {}

    private static ArenaManager instance = new ArenaManager();
    private ArrayList<Arena> arenas = new ArrayList<Arena>();
    List<String> arenasList = null;

    public static ArenaManager getArenaManager() {
        return instance;
    }

    public void setup() {
        arenasList = Settings.getSettings().getArenaFile().getStringList("Arena-List");

        for (String arenaName : arenasList) {
            // add each arena to the server
            Arena a = new Arena(arenaName);
            arenas.add(a);
            // set the value of that arena
            a.loadValues(Settings.getSettings().getArenaFile());
        }
        // save.
    }

    public Arena getArena(String name) {
        for (Arena a : arenas) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }

    public Arena getArena(Player player) {
        for (Arena a : arenas) {
            if (a.containsPlayer(player))
                return a;
        }
        return null;
    }

    public ArrayList<Arena> getArenas() {
        return arenas;
    }

    public void addNewArenaToConfig(Arena arena) {
        String[] steps = {"Red-Lobby", "Red-Spawn", "Blue-Lobby", "Blue-Spawn", "Max-Players", "Min-Players"};

        for (String value : steps) {
            Settings.getSettings().getArenaFile().set("Arenas." + arena.getName() + "." + value, "not_set");
        }
        arenas.add(arena);
        arenasList.add(arena.getName());
        Settings.getSettings().getArenaFile().set("Arena-List", arenasList);
        Settings.getSettings().saveArenaFile();
    }
}
