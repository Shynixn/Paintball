package me.synapz.paintball;


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
    List<String> arenasList = null;

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
            // a.removePlayersInArena();
            // a.broadcastMessage("Paintball has been disabled.");
        }
    }
    
    public void addNewArenaToConfig(Arena arena) {
        String[] steps = {"Name", "Spectate-Loc", "Red-Lobby", "Red-Spawn", "Blue-Lobby", "Blue-Spawn", "Max-Players", "Min-Players", "Is-Enabled"};
        int id = arena.getID();

        for (String value : steps) {
            if (value.equals("Name")) {
                Settings.getSettings().getArenaFile().set("Arenas." + id + "." + value, arena.getName());
                break;
            }
            if (value.equals("Is-Enabled")) {
                Settings.getSettings().getArenaFile().set("Arenas." + id + "." + value, false);
                break;
            }
            Settings.getSettings().getArenaFile().set("Arenas." + id + "." + value, "not_set");
        }
        arenas.add(arena);
        arenasList.add(arena.getName());
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
                case IN_LOBBY:
                    color += ChatColor.GREEN;
                    break;
                case STOPPED:
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

    public int getTotalAmountOfArenas() {
        return arenas.size();
    }

    private void loadArenas() {
        arenasList = Settings.getSettings().getArenaFile().getStringList("Arena-List");

        for (String arenaName : arenasList) {
            // add each arena to the server
            Arena a = new Arena(arenaName);
            try {
                // set the value of that arena
                a.loadValues(Settings.getSettings().getArenaFile());
            }catch (NullPointerException e) {
                /**
                 * An arena in 'Arena-List' is not found in 'Arenas'
                 * handle the error instead of breaking the whole plugin =).
                 * This Removes the arena that cant be loaded from 'Arena-List'
                 */
                arenasList.remove(arenaName);
                Settings.getSettings().getArenaFile().set("Arena-List", arenasList);
            }catch (Exception e) {
                Message.getMessenger().msg(Bukkit.getConsoleSender(), ChatColor.RED, "Error loading arenas.yml. Stacktrace: ");
                e.printStackTrace();
            }
            arenas.add(a);
        }
    }
}
