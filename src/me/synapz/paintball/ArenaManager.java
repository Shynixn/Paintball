package me.synapz.paintball;

import com.google.common.base.Joiner;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.locations.SignLocation;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;

import static me.synapz.paintball.storage.Settings.THEME;
import static org.bukkit.ChatColor.*;
import static me.synapz.paintball.locations.SignLocation.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenaManager {

    private ArenaManager() {}

    private static ArenaManager instance = new ArenaManager();

    // HashMap with arena's name to arena, makes it way more efficient to get an arena from a string
    private HashMap<String, Arena> arenas = new HashMap<>();

    public static ArenaManager getArenaManager() {
        return instance;
    }

    private Map<Location, SignLocation> leaderboardAndJoinSigns = new HashMap<>();

    // Sets up arenas
    public void setup() {
        loadArenas();
        loadSigns();
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
            a.forceRemovePlayers();
        }
    }

    // Gets the team list for an arena, the Integer is that team's score
    public List<Team> getTeamsList(Arena a) {
        List<Team> teamList = new ArrayList<>();
        for (String s : Settings.getSettings().getArenaFile().getStringList(a.getPath() + ".Teams")) {
            teamList.add(new Team(a, s));
        }
        return teamList;
    }

    // Adds a new arena to arenas.yml
    public void addNewArena(Arena arena) {
        Settings.getSettings().getArenaFile().set(arena.getPath() + "Name", arena.getName());
        Settings.getSettings().getArenaFile().set(arena.getPath() + "Enabled", false);

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
                case STARTING:
                    color += RED;
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
                a.loadValues();
            }catch (Exception e) {
                Message.getMessenger().msg(Bukkit.getConsoleSender(), false, RED, "Error loading " + arenaName + " in arenas.yml. Stacktrace: ");
                e.printStackTrace();
            }
            arenas.put(a.getName(), a);
        }
    }

    // Updates every type of sign (Leaderboard, Join, Autojoin)
    public void updateAllSignsOnServer() {
        long start = System.currentTimeMillis();
        String prefix = DARK_GRAY + "[" + THEME + "Paintball" + DARK_GRAY + "]";

        for (Arena a : getArenas().values()) {
            a.updateSigns();
        }

        for (SignLocation signLoc : leaderboardAndJoinSigns.values()) {
            if (!(signLoc.getLocation().getBlock().getState() instanceof Sign)) {
                signLoc.removeSign();
                return;
            }

            Sign sign = (Sign) signLoc.getLocation().getBlock().getState();
            switch (signLoc.getType()) {
                case AUTOJOIN:
                    sign.setLine(0, prefix); // in case the prefix changes
                    sign.update();
                    break;
                case LEADERBOARD:
                    // TODO: better way
                    StatType type = null;
                    for (StatType t : StatType.values()) {
                        if (t.getSignName().equalsIgnoreCase(sign.getLine(2))) {
                            type = t;
                        }
                    }
                    HashMap<String, String> playerAndStat = Settings.getSettings().getCache().getPlayerAtRank(Integer.parseInt(sign.getLine(0).replace("#", "")), type);
                    sign.setLine(1, playerAndStat.keySet().toArray()[0] + "");
                    sign.setLine(3, playerAndStat.values().toArray()[0] + "");
                    sign.update();
                    break;
                default:
                    break; // should never happen
            }
        }
    }

    public void addSign(SignLocation signLoc) {
        leaderboardAndJoinSigns.put(signLoc.getLocation(), signLoc);
    }

    public void removeSign(SignLocation signLoc) {
        leaderboardAndJoinSigns.remove(signLoc.getLocation(), signLoc);
    }

    public Map<Location, SignLocation> getSigns() {
        return leaderboardAndJoinSigns;
    }

    private void loadSigns() {
        FileConfiguration file = Settings.getSettings().getArenaFile();
        for (String rawLoc : file.getStringList("Signs.Autojoin")) {
            SignLocation signLoc = new SignLocation(SignLocations.AUTOJOIN, rawLoc);
            leaderboardAndJoinSigns.put(signLoc.getLocation(), signLoc);
        }

        for (String rawLoc : file.getStringList("Signs.Leaderboard")) {
            SignLocation signLoc = new SignLocation(SignLocations.LEADERBOARD, rawLoc);
            leaderboardAndJoinSigns.put(signLoc.getLocation(), signLoc);
        }
    }
}