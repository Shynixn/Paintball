package me.synapz.paintball.storage.files;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.enums.Databases;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.*;

public class UUIDFile extends PaintballFile {

    private final UUID uuid;

    public UUIDFile(UUID uuid) {
        super(JavaPlugin.getProvidingPlugin(Paintball.class), uuid.toString() + ".yml");

        this.uuid = uuid;
        Settings.getSettings().getPlayerDataFolder().addPlayerFile(this);
    }

    public UUIDFile(Plugin plugin, UUID uuid) {
        super(plugin, uuid.toString() + ".yml");

        this.uuid = uuid;
        Settings.getSettings().getPlayerDataFolder().addPlayerFile(this);
    }

    @Override
    public void onFirstCreate() {
        if (getFileConfig().getConfigurationSection("Player-Data." + uuid) == null) {
            getFileConfig().set("Player-Data." + uuid + ".Username", Bukkit.getOfflinePlayer(uuid).getName());
            // set the values to 0
            for (StatType value : StatType.values()) {
                if (!value.isCalculated())
                    getFileConfig().set(value.getPath(uuid), 0);
            }
        }
        // checks to see if their stats path is missing for a stat, useful for future upgrades with new stats
        for (StatType type : StatType.values()) {
            if (!type.isCalculated() && getFileConfig().getString(type.getPath(uuid)) == null)
                getFileConfig().set(type.getPath(uuid), 0);
        }
    }

    @Override
    public void saveFile() {
        if (Databases.SQL_ENABLED.getBoolean()) { // if sql is enabled, save Async to database
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
        if (Databases.SQL_ENABLED.getBoolean()) { // if sql is enabled, save sync to database
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
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getProvidingPlugin(Paintball.class), new Runnable() {
            @Override
            public void run() {
                try {
                    Settings.DATABASE.updateTable(fileConfig);
                } catch (SQLException e)  {
                    Messenger.error(Bukkit.getConsoleSender(), "Could not save " + getName() + " database.", "", "Stack trace");
                    e.printStackTrace();
                }
            }
        });
    }

    // Returns a player's stats in a Map with StatType holding the type connected to a String with it's value
    // Usedful for leaderboards and /pb stats
    public Map<StatType, String> getPlayerStats() {
        Map<StatType, String> stats = new HashMap<StatType, String>();
        boolean uuidNotFound = getFileConfig().getConfigurationSection("Player-Data." + uuid) == null;

        for (StatType type : StatType.values()) {
            if (uuidNotFound) // their uuid wasn't in file so they have no stats, so add 0 for everything
                stats.put(type, 0 + "");
            else
                stats.put(type, type == StatType.KD ? getKD(uuid) : type == StatType.ACCURACY ? getAccuracy(uuid) : getFileConfig().getString(type.getPath(uuid)));
        }
        return stats;
    }

    public UUID getUUID() {
        return uuid;
    }

    // Adds one to a player's stat
    // ex: if a player gets 1 kill, add one the stat in config
    public void incrementStat(StatType type, ArenaPlayer player) {
        UUID id = ((OfflinePlayer) player.getPlayer()).getUniqueId();

        switch (type) {
            // KD and ACCURACY are automatically determined by dividing
            case KD:
                return;
            case ACCURACY:
                return;
            case HIGEST_KILL_STREAK:
                getFileConfig().set(StatType.HIGEST_KILL_STREAK.getPath(id), player.getKillStreak());
                if (Databases.SQL_ENABLED.getBoolean()) saveAsynchronously();
                return;
            case GAMES_PLAYED:
                if (player.isWinner())
                    addOneToPath(StatType.WINS.getPath(id));
                else if (player.isTie())
                    addOneToPath(StatType.TIES.getPath(id));
                else
                    addOneToPath(StatType.DEFEATS.getPath(id));
                break; // not return; because it still has to increment the games played
        }

        addOneToPath(type.getPath(id));
        if (Databases.SQL_ENABLED.getBoolean()) saveAsynchronously();
    }

    public void addToStat(StatType type, ArenaPlayer arenaPlayer, int toAdd) {

        UUID id = ((OfflinePlayer) arenaPlayer.getPlayer()).getUniqueId();

        getFileConfig().set(type.getPath(id), getFileConfig().getInt(type.getPath(id)) + toAdd);
        if (Databases.SQL_ENABLED.getBoolean()) saveAsynchronously();
    }

    public void setStat(StatType type, ArenaPlayer arenaPlayer, int toSet) {
        UUID id = ((OfflinePlayer) arenaPlayer.getPlayer()).getUniqueId();

        getFileConfig().set(type.getPath(id), toSet);
        if (Databases.SQL_ENABLED.getBoolean()) saveAsynchronously();
    }

    // Resets a specific stat
    public void resetStats(StatType type, OfflinePlayer player) {
        UUID id = player.getUniqueId();

        if (!type.isCalculated()) {
            getFileConfig().set(type.getPath(id), 0);
            if (Databases.SQL_ENABLED.getBoolean()) saveAsynchronously();
        }
    }

    public String getStats(StatType type) {
        return getPlayerStats().get(type);
    }

    // Returns a player's KD by dividing kills and deaths
    public String getKD(UUID id) {
        int kills = getFileConfig().getInt(StatType.KILLS.getPath(id));
        int deaths = getFileConfig().getInt(StatType.DEATHS.getPath(id));
        return String.format("%.2f", Utils.divide(kills, deaths));
    }

    // Returns a player's accuracy by dividing shots and hits
    public String getAccuracy(UUID id) {
        int shots = getFileConfig().getInt(StatType.SHOTS.getPath(id));
        int hits = getFileConfig().getInt(StatType.HITS.getPath(id));
        return String.format("%d%s", (int) Math.round(Utils.divide(hits, shots)*100), "%");
    }

    // Saves player information to PlayerData file
    // Called when the player enters an arena
    public void savePlayerInformation(Player player) {
        /*
        ExperienceManager exp = new ExperienceManager(player);
        String id = player.getName();

        if (locations.containsKey(id))
            return;

        locations.put(id, player.getLocation());
        gamemodes.put(id, player.getGameMode());
        foodLevels.put(id, player.getFoodLevel());
        health.put(id, player.getHealth());
        inventories.put(id, player.getInventory().getContents());
        armour.put(id, player.getInventory().getArmorContents());
        expLevels.put(id, exp.getCurrentExp());
        scoreboards.put(id, player.getScoreboard() == null ? Bukkit.getScoreboardManager().getNewScoreboard() : player.getScoreboard());
        healthScale.put(id, player.getHealthScale());
        allowFly.put(id,  player.getAllowFlight());
        flying.put(id, player.isFlying());
        potions.put(id, player.getActivePotionEffects());

        addStatsIfNotYetAdded(player.getUniqueId());
        Utils.stripValues(player);
        */
    }

    // Restores all of the player's settings, then sets the info to null
    public void restorePlayerInformation(Player player) {
        /*
        String id = player.getName();

        if (!locations.containsKey(id))
            return;

        Utils.stripValues(player);
        ExperienceManager exp = new ExperienceManager(player);

        player.teleport(locations.get(id));
        player.getInventory().setContents(inventories.get(id));
        player.getInventory().setArmorContents(armour.get(id));
        player.setFoodLevel(foodLevels.get(id));
        player.setGameMode(gamemodes.get(id));
        player.setScoreboard(scoreboards.get(id));
        player.setAllowFlight(allowFly.get(id));
        player.setFlying(flying.get(id));
        exp.setExp(expLevels.get(id));
        player.addPotionEffects(potions.get(id));

        if (health.get(id) > 20d || health.get(id) < 0) {
            player.setHealth(20);
        } else {
            player.setHealth(health.get(id));
        }

        player.setHealthScale(healthScale.get(id));

        locations.remove(id);
        gamemodes.remove(id);
        foodLevels.remove(id);
        health.remove(id);
        inventories.remove(id);
        expLevels.remove(id);
        scoreboards.remove(id);
        flying.remove(id);
        player.updateInventory();
        */
    }

    // Increments the set path by one
    private void addOneToPath(String path) {
        getFileConfig().set(path, getFileConfig().getInt(path) + 1);
        if (Databases.SQL_ENABLED.getBoolean()) saveAsynchronously();
    }

    private void setValue(String path, Object object) {
        fileConfig.set(path, object);
    }
}
