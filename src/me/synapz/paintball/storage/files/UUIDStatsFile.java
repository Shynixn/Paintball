package me.synapz.paintball.storage.files;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.enums.Databases;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UUIDStatsFile extends PaintballFile {

    private final UUID uuid;
    private Scoreboard cachedScoreboard;

    public UUIDStatsFile(UUID uuid) {
        super(Paintball.getInstance(), "/stats/" + uuid + ".yml");

        this.uuid = uuid;
        Settings.getSettings().getStatsFolder().addPlayerFile(this);
    }

    @Override
    public void onFirstCreate() {
        UUID uuid = UUID.fromString(this.getName().replace(".yml", "").replace("/stats/", ""));

        if (getFileConfig().getConfigurationSection("Player-Data") == null) {
            getFileConfig().set("UUID", uuid.toString());
            getFileConfig().set("Username", Bukkit.getOfflinePlayer(uuid).getName());
            // set the values to 0
            for (StatType value : StatType.values()) {
                if (!value.isCalculated())
                    getFileConfig().set(value.getPath(), 0);
            }
        }
        // checks to see if their stats path is missing for a stat, useful for future upgrades with new stats
        for (StatType type : StatType.values()) {
            if (!type.isCalculated() && getFileConfig().getString(type.getPath()) == null)
                getFileConfig().set(type.getPath(), 0);
        }
    }

    @Override
    public void saveFile() {
        if (Databases.ENABLED.getBoolean()) { // if sql is enabled, save Async to database
            try {
                saveAsynchronously();
            }
            catch (IllegalPluginAccessException e) {
                shutdown();
            }
        } else { // else, save sync to local
            super.saveFile();
        }
    }

    public void shutdown() {
        if (Databases.ENABLED.getBoolean()) { // if sql is enabled, save sync to database
            try {
                Settings.DATABASE.updateTable(fileConfig);
            } catch (SQLException e)  {
                Messenger.error(Bukkit.getConsoleSender(), "Could not save " + getName() + " database.", "", "Stack trace");
                e.printStackTrace();
            }
        } else { // else, save sync to local
            super.saveFile();
        }
    }

    public void saveAsynchronously() {
        Bukkit.getScheduler().runTaskAsynchronously(Paintball.getInstance(), () -> {
            try {
                Settings.DATABASE.updateTable(fileConfig);
            } catch (SQLException e)  {
                Messenger.error(Bukkit.getConsoleSender(), "Could not save " + getName() + " database.", "", "Stack trace");
                e.printStackTrace();
            }
        });
    }

    // Returns a player's stats in a Map with StatType holding the type connected to a String with it's value
    // Usedful for leaderboards and /pb stats
    public Map<StatType, String> getPlayerStats() {
        Map<StatType, String> stats = new HashMap<>();

        for (StatType type : StatType.values())
            stats.put(type, type == StatType.KD ? getKD() : type == StatType.ACCURACY ? getAccuracy() : getFileConfig().getString(type.getPath(), "0"));

        return stats;
    }

    public UUID getUUID() {
        return uuid;
    }

    // Adds one to a player's stat
    // ex: if a player gets 1 kill, add one the stat in config
    public void  incrementStat(StatType type, ArenaPlayer player) {
        switch (type) {
            // KD and ACCURACY are automatically determined by dividing
            case KD:
                return;
            case ACCURACY:
                return;
            case HIGEST_KILL_STREAK:
                getFileConfig().set(StatType.HIGEST_KILL_STREAK.getPath(), player.getKillStreak());
                if (Databases.ENABLED.getBoolean()) saveAsynchronously();
                return;
            case GAMES_PLAYED:
                if (player.isWinner())
                    addOneToPath(StatType.WINS.getPath());
                else if (player.isTie())
                    addOneToPath(StatType.TIES.getPath());
                else
                    addOneToPath(StatType.DEFEATS.getPath());
                break; // not return; because it still has to increment the games played
        }

        addOneToPath(type.getPath());
        if (Databases.ENABLED.getBoolean()) saveAsynchronously();
    }

    public void addToStat(StatType type, int toAdd) {
        getFileConfig().set(type.getPath(), getFileConfig().getInt(type.getPath()) + toAdd);
        if (Databases.ENABLED.getBoolean()) saveAsynchronously();
    }

    public void setStat(StatType type, int toSet) {
        getFileConfig().set(type.getPath(), toSet);
        if (Databases.ENABLED.getBoolean()) saveAsynchronously();
    }

    // Resets a specific stat
    public void resetStats(StatType type) {
        if (!type.isCalculated()) {
            getFileConfig().set(type.getPath(), 0);
            if (Databases.ENABLED.getBoolean()) saveAsynchronously();
        }
    }

    public String getStats(StatType type) {
        return getPlayerStats().get(type);
    }

    // Returns a player's KD by dividing kills and deaths
    public String getKD() {
        int kills = getFileConfig().getInt(StatType.KILLS.getPath());
        int deaths = getFileConfig().getInt(StatType.DEATHS.getPath());
        return String.format("%.2f", Utils.divide(kills, deaths)).replace(",", ".");
    }

    // Returns a player's accuracy by dividing shots and hits
    public String getAccuracy() {
        int shots = getFileConfig().getInt(StatType.SHOTS.getPath());
        int hits = getFileConfig().getInt(StatType.HITS.getPath());
        return String.format("%d%s", (int) Math.round(Utils.divide(hits, shots)*100), "%");
    }

    // Increments the set path by one
    private void addOneToPath(String path) {
        getFileConfig().set(path, getFileConfig().getInt(path) + 1);
        if (Databases.ENABLED.getBoolean()) saveAsynchronously();
    }

    public void setFileConfig(FileConfiguration config) {
        this.fileConfig = config;
    }
}
