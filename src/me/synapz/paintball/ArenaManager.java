package me.synapz.paintball;

import com.google.common.base.Joiner;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ArenaManager {

    private ArenaManager() {}

    private static ArenaManager instance = new ArenaManager();

    // HashMap with arena's name to arena, makes it way more efficient to get an arena from a string
    private HashMap<String, Arena> arenas = new HashMap<>();

    public static ArenaManager getArenaManager() {
        return instance;
    }

    // Sets up arenas
    public void setup() {
        loadArenas();
    }

    // Gets an arena from a name
    public Arena getArena(String name) {
        return arenas.get(name);
    }

    // Gets an arena from a player inside it
    public Arena getArena(Player player) {
        for (Arena a : arenas.values()) {
            if (a.containsPlayer(player))
                return a;
        }
        return null;
    }

    // Gets a list of all arenas
    public HashMap<String, Arena> getArenas() {
        return arenas;
    }

    // Stops all arenas
    public void stopArenas() {
        for (Arena a : getArenas().values()) {
            for (PaintballPlayer player : a.getAllPlayers().values()) {
                player.leaveArena();
            }
        }
    }

    // Gets the team list for an arena, the Integer is that team's score
    public HashMap<Team, Integer> getTeamsList(Arena a) {
        HashMap<Team, Integer> teamList = new HashMap<Team, Integer>();
        for (String s : Settings.getSettings().getArenaFile().getStringList(a.getPath() + ".Teams")) {
            teamList.put(new Team(a, s), 0);
        }
        return teamList;
    }

    // Adds a new arena to arenas.yml
    public void addNewArena(Arena arena) {
        String[] steps = {"Name", "Spectate-Loc", "Max-Players", "Min-Players", "Is-Enabled", "Teams"};
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
        arenas.put(arena.getName(), arena);
        Settings.getSettings().addNewConfigSection(arena);
        Settings.getSettings().saveArenaFile();
    }

    // Get a readable list and send it to the param player
    public void getList(Player player) {
        List<String> list = new ArrayList<String>();

        if (getArenas().size() == 0) {
            Message.getMessenger().msg(player, false, BLUE, "There are currently no arenas.");
            return;
        }

        for (Arena a : getArenas().values()) {
            String color = "";

            switch (a.getState()) {
                case WAITING:
                    color += GREEN;
                    break;
                case IN_PROGRESS:
                    color += RED;
                    break;
                case DISABLED:
                    color += GRAY;
                    break;
                case NOT_SETUP:
                    color += STRIKETHROUGH + "" + GRAY;
                    break;
                default:
                    color += RED;
                    break;
            }
            list.add(ChatColor.RESET + "" + color + a.getName());
        }

        String out = Joiner.on(GRAY + ", ").join(list);
        Message.getMessenger().msg(player, false, GRAY, BLUE + "Arenas: " + out,
                GREEN + "█-" + GRAY + "Joinable " + RED + "█-" + GRAY + "InProgress " + GRAY + "█-" + GRAY + "Disabled/Not-Setup");
    }

    // Load all arenas from arenas.yml
    private void loadArenas() {
        FileConfiguration file = Settings.getSettings().getArenaFile();
        Set<String> rawArenas = file.getConfigurationSection("Arenas") == null ? null : file.getConfigurationSection("Arenas").getKeys(false);

        if (rawArenas == null) {
            return;
        }

        for (String arenaName : rawArenas) {
            Arena a = null;
            String name = file.getString("Arenas." + arenaName + ".Name");
            try {
                // add each arena to the server
                a = new Arena(arenaName, name);
                // set the value of that arena
                a.loadValues(file);
            }catch (Exception e) {
                Message.getMessenger().msg(Bukkit.getConsoleSender(), false, RED, "Error loading " + arenaName + " in arenas.yml. Stacktrace: ");
                e.printStackTrace();
            }
            arenas.put(a.getName(), a);
        }
    }

    // Stores an arena's sign into areans.yml so it can be easily updated
    // TODO: also store leaderboard signs...
    public void storeSignLocation(Location loc, Arena a) {
        List<String> signsList = Settings.getSettings().getArenaFile().getStringList(a.getPath() + "Sign-Locs");
        String locString = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getPitch() + "," + loc.getYaw();
        if (signsList == null)
            signsList = new ArrayList<String>();
        if (signsList.contains(locString)) return;
        signsList.add(locString);
        Settings.getSettings().getArenaFile().set(a.getPath() + "Sign-Locs", signsList);
        Settings.getSettings().saveArenaFile();
    }

    // Remove a sign location from arenas.yml
    public void removeSignLocation(Location loc, Arena a) {
        List<String> signsList = Settings.getSettings().getArenaFile().getStringList(a.getPath() + "Sign-Locs");
        String locString = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getPitch() + "," + loc.getYaw();
        if (signsList == null || !(signsList.contains(locString))) {
            return;
        }
        signsList.remove(locString);
        Settings.getSettings().getArenaFile().set(a.getPath() + "Sign-Locs", signsList);
        Settings.getSettings().saveArenaFile();
    }
}