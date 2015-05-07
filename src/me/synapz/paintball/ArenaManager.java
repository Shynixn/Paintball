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
        int errors = 0;
        arenasList = Settings.getSettings().getArenaFile().getStringList("Arena-List");

        for (String arenaName : arenasList) {
            // add each arena to the server
            Arena a = new Arena(arenaName);
            arenas.add(a);
            // set the value of that arena
            try {
                a.loadValues(Settings.getSettings().getArenaFile());
            }catch (NullPointerException e) {
                /**
                 * An arena in 'Arenas' is not found in 'Arena-List'
                 * So it throws an error, just warn console instead of breaking the
                 * whole plugin =)
                 */
                errors++;
            }
        }
        if (errors > 1) { // Used to prevent spamming to console by keeping the message out of the for loop
            Message.getMessenger().msg(Bukkit.getConsoleSender(), ChatColor.RED, errors + (errors > 1 ? " errors. ": " error. ") + "Corrupted arenas.yml file.", "Reason: An arena found in the 'Arena-List' path is not " +
                    "found in 'Arenas' path.");
        }
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
            a.removePlayersInArena();
            // a.broadcastMessage("Paintball has been disabled.");
        }
    }
    
    public void addNewArenaToConfig(Arena arena) {
        String[] steps = {"Spectate-Loc", "Red-Lobby", "Red-Spawn", "Blue-Lobby", "Blue-Spawn", "Max-Players", "Min-Players", "Is-Enabled"};

        for (String value : steps) {
            if (value.equals(steps[7])) {
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
}
