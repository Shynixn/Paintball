package me.synapz.paint.arenas;


import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ArenaManager {

    public enum Team {
        RED,
        BLUE;
    }
    private ArenaManager() {

    }

    private static ArenaManager instance = new ArenaManager();
    private ArrayList<Arena> arenas = new ArrayList<Arena>();

    public static ArenaManager getArenaManager() {
        return instance;
    }

    public void setup() {
        // Setup config
        // add all arenas to arraylist
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

    public void addArena(Arena arena) {
        arenas.add(arena);
    }

}
