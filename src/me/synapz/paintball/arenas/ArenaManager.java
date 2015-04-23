package me.synapz.paintball.arenas;


import me.synapz.paintball.Message;
import me.synapz.paintball.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
        String[] steps = {"Red-Lobby", "Red-Spawn", "Blue-Lobby", "Blue-Spawn", "Max-Players", "Min-Players", "Is-Enabled"};

        for (String value : steps) {
            if (value.equals(steps[6])) {
                Settings.getSettings().getArenaFile().set("Arenas." + arena.getName() + "." + value, false);
                break;
            }
            Settings.getSettings().getArenaFile().set("Arenas." + arena.getName() + "." + value, "not_set");
        }
        arenas.add(arena);
        arenasList.add(arena.getName());
        Settings.getSettings().getArenaFile().set("Arena-List", arenasList);
        Settings.getSettings().saveArenaFile();
    }

    public void getList(Player player, int page) {
        String arenas = "";

        for (Arena a : getArenas()) {
            String color = "";

            switch (a.getState()) {
                case IN_PROGRESS:
                    color += ChatColor.RED;
                    break;
                case DISABLED:
                    color += ChatColor.GRAY;
                    break;
                case IN_LOBBY:
                    color += ChatColor.GREEN;
                    break;
                case STOPPED:
                    color += ChatColor.GREEN;
                    break;
                case NOT_SETUP:
                    color += ChatColor.STRIKETHROUGH + "" + ChatColor.GRAY;
                    break;
            }
            arenas = arenas + ", " + color + a.getName();
        }

        if (arenas.equals("")) {
            arenas = "There are currently no arenas.";
            Message.getMessenger().msg(player, ChatColor.BLUE, arenas);
            return;
        }

        arenas = arenas.substring(2, arenas.length());
        // arenas = arenas.substring(page * 10, page * 10 + 10); can't figure out the correct way
        Message.getMessenger().msg(player, ChatColor.GRAY, "Page: " + Message.getMessenger().THEME + page + "/" + Math.round(getArenas().size() / 10), ChatColor.BLUE + "Arenas: " + arenas);
    }
}
