package me.synapz.paintball.storage;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Message;
import me.synapz.paintball.Team;
import me.synapz.paintball.locations.SignLocation;
import me.synapz.paintball.locations.TeamLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        List<String> valuesToSet = new ArrayList<String>() {{
            this.add("max-score");
            this.add("time");
            this.add("win-waiting-time");
            this.add("kill-coin-shop");
            this.add("safe-time");
            this.add("hits-to-kill");

            this.add("Join-Arena.give-wool-helmet");
            this.add("Join-Arena.color-player-title");
            this.add("Join-Arena.per-team-chat");

            this.add("Join-Lobby.give-wool-helmet");
            this.add("Join-Lobby.color-player-title");
            this.add("Join-Lobby.give-team-switcher");
            this.add("Join-Lobby.per-team-chat");

            this.add("Rewards.Kill-Coins.per-kill");
            this.add("Rewards.Kill-Coins.per-death");
            this.add("Rewards.Money.per-kill");
            this.add("Rewards.Money.per-death");
            this.add("Rewards.Money.per-win");
            this.add("Rewards.Money.per-defeat");

            this.add("Chat.use-arena-chat");
            this.add("Chat.broadcast-winner");
            this.add("Chat.spectator-chat");
            this.add("Chat.arena-chat");

            this.add("Countdown.lobby.countdown");
            this.add("Countdown.lobby.interval");
            this.add("Countdown.lobby.no-interval");

            this.add("Countdown.arena.countdown");
            this.add("Countdown.arena.interval");
            this.add("Countdown.arena.no-interval");
        }};

        for (String value : valuesToSet) {
            fileConfig.set(getConfigPath(value, a), "default");
        }
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
                Message.getMessenger().msg(Bukkit.getConsoleSender(), false, RED, "Error loading " + arenaName + " in arenas.yml. Stacktrace: ");
                e.printStackTrace();
            }
            ArenaManager.getArenaManager().getArenas().put(a.getName(), a);
        }
    }

    // TODO: put this stuff in the arenafile class
    public int loadInt(String item, Arena arena) {
        if (fileConfig.getString(getConfigPath(item, arena)) != null && fileConfig.getString(getConfigPath(item, arena)).equalsIgnoreCase("default")) {
            return Settings.getSettings().getConfig().getInt(getArenaConfigPath(item));
        } else {
            return fileConfig.getInt(getConfigPath(item, arena));
        }
    }

    public String loadString(String item, Arena arena) {
        if (fileConfig.getString(getConfigPath(item, arena)) != null && fileConfig.getString(getConfigPath(item, arena)).equalsIgnoreCase("default")) {
            return ChatColor.translateAlternateColorCodes('&', Settings.getSettings().getConfig().getString(getArenaConfigPath(item)));
        } else {
            return ChatColor.translateAlternateColorCodes('&', fileConfig.getString(getConfigPath(item, arena)));
        }
    }

    public boolean loadBoolean(String item, Arena arena) {
        if (fileConfig.getString(getConfigPath(item, arena)) != null && fileConfig.getString(getConfigPath(item, arena)).equalsIgnoreCase("default")) {
            return Settings.getSettings().getConfig().getBoolean(getArenaConfigPath(item));
        } else {
            return fileConfig.getBoolean(getConfigPath(item, arena));
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

    private String getConfigPath(String value, Arena arena) {
        return arena.getPath() + ".Config." + value;
    }

    private String getArenaConfigPath(String value) {
        return  "Per-Arena-Settings.Defaults." + value;
    }
}
