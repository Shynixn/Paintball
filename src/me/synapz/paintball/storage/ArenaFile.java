package me.synapz.paintball.storage;

import me.synapz.paintball.*;
import me.synapz.paintball.locations.SignLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

import static me.synapz.paintball.storage.Settings.ARENA;

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
        for (String rawItem : fileConfig.getStringList(a.getPath() + ".Teams")) {
            String colorCode = rawItem.split(":")[0]; // rawItem will be stored as, colorCode:teamName like, &c:Red
            String teamName = rawItem.split(":")[1];
            teamList.add(new Team(a, colorCode, teamName));
        }
        return teamList;
    }

    // Adds a new arena to arenas.yml
    public void addNewArenaToFile(Arena arena) {
        fileConfig.set(arena.getPath() + "Name", arena.getName());
        fileConfig.set(arena.getPath() + "Enabled", false);

        ArenaManager.getArenaManager().getArenas().put(arena.getName(), arena);
        addNewConfigSection(arena);
        saveFile();
    }

    // Adds a new arena to arena.yml with values default
    public void addNewConfigSection(Arena a) {
        a.loadConfigValues();
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
                a = new Arena(arenaName, name, false);
                // set the value of that arena
                a.loadValues();
            }catch (Exception e) {
                Messenger.error(Bukkit.getConsoleSender(), "Error loading " + arenaName + " in arenas.yml. Stacktrace: ");
                e.printStackTrace();
            }
            ArenaManager.getArenaManager().getArenas().put(a.getName(), a);
        }
    }

    // TODO: put this stuff in the arenafile class
    public int loadInt(String item, Arena arena) {
        return (int) loadValue(item, arena);
    }

    public String loadString(String item, Arena arena) {
        return ChatColor.translateAlternateColorCodes('&', (String) loadValue(item, arena));
    }

    public boolean loadBoolean(String item, Arena arena) {
        return (boolean) loadValue(item, arena);
    }

    private Object loadValue(String item, Arena arena) {
        Map<String, File> allFiles = new HashMap<String, File>(){{
            for (File file : JavaPlugin.getProvidingPlugin(Paintball.class).getDataFolder().listFiles())
                put(file.getName(), file);
        }};

        String path = getConfigPath(item, arena);
        boolean notFoundInArena = fileConfig.get(path) == null;
        boolean notFoundInConfig = YamlConfiguration.loadConfiguration(allFiles.get("config.yml")).get(getArenaConfigPath(item)) == null;

        if (notFoundInArena) {
            fileConfig.set(path, "default");
        }

        /*
            Since config.yml cannot be saved or it removes its format,
            rename the config file and make a new one
        */
        if (notFoundInConfig) {
            Settings.getSettings().backupConfig();
        }

        if (fileConfig.getString(path).equalsIgnoreCase("default"))
            return Settings.getSettings().getConfig().get(getArenaConfigPath(item));
        else
            return fileConfig.get(path);
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

    private String getConfigPath(String value, Arena arena) {
        return arena.getPath() + ".Config." + value;
    }

    private String getArenaConfigPath(String value) {
        return  "Per-Arena-Settings.Defaults." + value;
    }
}
