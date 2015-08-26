package me.synapz.paintball;


import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
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
    private List<String> arenasList = null;

    public static ArenaManager getArenaManager() {
        return instance;
    }

    public void setup() {
        loadArenas();
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

    public void stopArenas() {
        for (Arena a : getArenas()) {
            a.removePlayers();
        }
    }
    
    public void addNewArenaToConfig(Arena arena) {
        String[] steps = {"Name", "Spectate-Loc", "Red-Lobby", "Red-Spawn", "Blue-Lobby", "Blue-Spawn", "Max-Players", "Min-Players", "Is-Enabled"};
        String id = arena.getDefaultName();

        for (String value : steps) {
            if (value.equals("Name")) {
                Settings.getSettings().getArenaFile().set("Arenas." + id + "." + value, arena.getName());
            } else if (value.equals("Is-Enabled")) {
                Settings.getSettings().getArenaFile().set("Arenas." + id + "." + value, false);
            }else {
                Settings.getSettings().getArenaFile().set("Arenas." + id + "." + value, "not_set");
            }
        }
        arenas.add(arena);
        arenasList.add(id + ":" + id);
        Settings.getSettings().getArenaFile().set("Arena-List", arenasList);
        Settings.getSettings().saveArenaFile();
    }

    public void getList(Player player) {
        ChatColor gr = ChatColor.GRAY;
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
                case WAITING:
                    color += ChatColor.GREEN;
                    break;
                case NOT_SETUP:
                    color += ChatColor.STRIKETHROUGH;
                    break;
            }
            arenas += ChatColor.GRAY + ", " + color + a.getName();
        }

        if (arenas.equals("")) {
            Message.getMessenger().msg(player, ChatColor.BLUE, "There are currently no arenas.");
            return;
        }
        arenas = arenas.substring(4, arenas.length());

        Message.getMessenger().msg(player, ChatColor.GRAY, ChatColor.BLUE + "Arenas: " + ChatColor.GRAY + arenas,
                ChatColor.GREEN + "█-" + gr + "Joinable " + ChatColor.RED + "█-" + gr + "InProgress " + gr + "█-" + gr + "Disabled/Not-Setup");
    }

    private void loadArenas() {
        arenasList = Settings.getSettings().getArenaFile().getStringList("Arena-List");
        Arena a = null;


        for (String arenaName : arenasList) {
            String defaultName = arenaName.substring(0, arenaName.lastIndexOf(":"));
            String name = arenaName.substring(arenaName.lastIndexOf(":")+1, arenaName.length());
            try {
                // add each arena to the server
                a = new Arena(defaultName, name);

                // set the value of that arena
                a.loadValues(Settings.getSettings().getArenaFile());
            }catch (Exception e) {
                Message.getMessenger().msg(Bukkit.getConsoleSender(), ChatColor.RED, "Error loading " + arenaName + " in.         arenas.yml. Stacktrace: ");
                e.printStackTrace();
            }
            arenas.add(a);
        }
    }
}
