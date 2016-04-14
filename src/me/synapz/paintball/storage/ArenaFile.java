package me.synapz.paintball.storage;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.arenas.*;
import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.locations.HologramLocation;
import me.synapz.paintball.locations.SignLocation;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

import static me.synapz.paintball.storage.Settings.PLAYERDATA;

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
        fileConfig.set(arena.getPath() + "Type", arena.getArenaType().getShortName());

        ArenaManager.getArenaManager().getArenas().put(arena.getName(), arena);
        addNewConfigSection(arena);
        saveFile();
    }

    public void loadLeaderboards() {
        for (String loc : getHologramList()) {
            HologramLocation hologramLocation = new HologramLocation(loc);
            addLeaderboard(hologramLocation.getLocation(), hologramLocation.getType(), hologramLocation.getPage(), false);
        }
    }

    public void addLeaderboard(Location loc, StatType statType, int page, boolean addToFile) {
        Hologram hologram = HologramsAPI.createHologram(JavaPlugin.getProvidingPlugin(Paintball.class), loc);

        for (String statLine : PLAYERDATA.getPage(statType, page)) {
            hologram.appendTextLine(statLine);
        }

        new HologramLocation(loc, statType, page, addToFile);
    }

    public void deleteLeaderboards() {
        for (Hologram hologram : HologramsAPI.getHolograms(JavaPlugin.getProvidingPlugin(Paintball.class))) {
            hologram.delete();
        }
    }

    public List<String> getHologramList() {
        return Settings.ARENA_FILE.getStringList("Hologram-Locations");
    }

    // Adds a new arena to arena.yml with values default
    public void addNewConfigSection(Arena a) {
        a.loadConfigValues();
    }

    // Load all arenas from arenas.yml
    public void loadArenasFromFile() {
        Set<String> rawArenas = fileConfig.getConfigurationSection("Arenas") == null ? null : fileConfig.getConfigurationSection("Arenas").getKeys(false);

        if (rawArenas == null) {
            return;
        }

        for (String arenaName : rawArenas) {
            Arena a = null;
            String name = fileConfig.getString("Arenas." + arenaName + ".Name");
            ArenaType type = ArenaType.getArenaType(null, fileConfig.getString("Arenas." + arenaName + ".Type"));
            try {
                // add each arena to the server

                switch (type) {
                    case CTF:
                        a = new CTFArena(arenaName, name, false);
                        break;
                    case DOM:
                        a = new DomArena(arenaName, name, false);
                        break;
                    case RTF:
                        a = new RTFArena(arenaName, name, false);
                        break;
                    case TDM:
                        a = new Arena(arenaName, name, false);
                        break;
                    case FFA:
                        a = new FFAArena(arenaName, name, false);
                        break;
                    case LTS:
                        a = new LTSArena(arenaName, name, false);
                        break;
                    default:
                        a = new Arena(arenaName, name, false);
                        break;
                }

                // set the value of that arena
                a.loadValues();
            }catch (Exception e) {
                Messenger.error(Bukkit.getConsoleSender(), "Error loading " + arenaName + " in arenas.yml. Stacktrace: ");
                e.printStackTrace();
            }
            ArenaManager.getArenaManager().getArenas().put(a.getName(), a);
        }
    }

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
            Settings.getSettings().backupConfig("config");
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
