package me.synapz.paintball.storage;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Message;
import me.synapz.paintball.Team;
import me.synapz.paintball.locations.SignLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.*;

import static org.bukkit.ChatColor.RED;

public class ArenaFile extends PaintballFile {

    private Map<Location, SignLocation> leaderboardAndJoinSigns = new HashMap<>();

    public ArenaFile(Plugin pb) {
        super(pb, "arenas.yml");
    }

    // Sets up arenas from arenas.yml
    public void setup() {
        loadArenasFromFile();
        loadSigns();
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

    // Gets the team list for an arena, the Integer is that team's score
    public List<Team> getTeamsList(Arena a) {
        List<Team> teamList = new ArrayList<>();
        for (String s : fileConfig.getStringList(a.getPath() + ".Teams")) {
            teamList.add(new Team(a, s));
        }
        return teamList;
    }

    // Adds a new arena to arenas.yml
    public void addNewArenaToFile(Arena arena) {
        fileConfig.set(arena.getPath() + "Name", arena.getName());
        fileConfig.set(arena.getPath() + "Enabled", false);

        ArenaManager.getArenaManager().getArenas().put(arena.getName(), arena);
        Settings.getSettings().addNewConfigSection(arena);
        saveFile();
    }

    // Load all arenas from arenas.yml
    private void loadArenasFromFile() {
        Set<String> rawArenas = fileConfig.getConfigurationSection("Arenas") == null ? null : fileConfig.getConfigurationSection("Arenas").getKeys(false);

        if (rawArenas == null) {
            return;
        }

        for (String arenaName : rawArenas) {
            Arena a = null;
            String name = fileConfig.getString("Arenas." + arenaName + ".Name");
            try {
                // add each arena to the server
                a = new Arena(arenaName, name);
                // set the value of that arena
                a.loadValues();
            }catch (Exception e) {
                Message.getMessenger().msg(Bukkit.getConsoleSender(), false, RED, "Error loading " + arenaName + " in arenas.yml. Stacktrace: ");
                e.printStackTrace();
            }
            ArenaManager.getArenaManager().getArenas().put(a.getName(), a);
        }
    }

    private void loadSigns() {
        for (String rawLoc : fileConfig.getStringList("Signs.Autojoin")) {
            SignLocation signLoc = new SignLocation(SignLocation.SignLocations.AUTOJOIN, rawLoc);
            leaderboardAndJoinSigns.put(signLoc.getLocation(), signLoc);
        }

        for (String rawLoc : fileConfig.getStringList("Signs.Leaderboard")) {
            SignLocation signLoc = new SignLocation(SignLocation.SignLocations.LEADERBOARD, rawLoc);
            leaderboardAndJoinSigns.put(signLoc.getLocation(), signLoc);
        }
    }
}
