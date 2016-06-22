package me.synapz.paintball.storage.files;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.enums.Databases;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.locations.PaintballLocation;
import me.synapz.paintball.locations.PlayerLocation;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.ExperienceManager;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.Array;
import java.sql.SQLException;
import java.util.*;

public class UUIDFile extends PaintballFile {

    private final UUID uuid;

    public UUIDFile(UUID uuid) {
        super(JavaPlugin.getProvidingPlugin(Paintball.class), "/playerdata/" + uuid + ".yml");

        this.uuid = uuid;
        Settings.getSettings().getPlayerDataFolder().addPlayerFile(this);
    }

    @Override
    public void onFirstCreate() {
        UUID uuid = UUID.fromString(this.getName().replace(".yml", "").replace("/playerdata/", ""));

        if (getFileConfig().getConfigurationSection("Player-Data") == null) {
            getFileConfig().set("Player-Data.UUID", uuid.toString());
            getFileConfig().set("Player-Data.Username", Bukkit.getOfflinePlayer(uuid).getName());
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
        Map<StatType, String> stats = new HashMap<>();
        boolean uuidNotFound = getFileConfig().getConfigurationSection("Player-Data") == null;

        for (StatType type : StatType.values()) {
            if (uuidNotFound) // their uuid wasn't in file so they have no stats, so add 0 for everything
                stats.put(type, 0 + "");
            else
                stats.put(type, type == StatType.KD ? getKD(uuid) : type == StatType.ACCURACY ? getAccuracy(uuid) : getFileConfig().getString(type.getPath()));
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
                getFileConfig().set(StatType.HIGEST_KILL_STREAK.getPath(), player.getKillStreak());
                if (Databases.SQL_ENABLED.getBoolean()) saveAsynchronously();
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
        if (Databases.SQL_ENABLED.getBoolean()) saveAsynchronously();
    }

    public void addToStat(StatType type, ArenaPlayer arenaPlayer, int toAdd) {

        UUID id = ((OfflinePlayer) arenaPlayer.getPlayer()).getUniqueId();

        getFileConfig().set(type.getPath(), getFileConfig().getInt(type.getPath()) + toAdd);
        if (Databases.SQL_ENABLED.getBoolean()) saveAsynchronously();
    }

    public void setStat(StatType type, ArenaPlayer arenaPlayer, int toSet) {
        UUID id = ((OfflinePlayer) arenaPlayer.getPlayer()).getUniqueId();

        getFileConfig().set(type.getPath(), toSet);
        if (Databases.SQL_ENABLED.getBoolean()) saveAsynchronously();
    }

    // Resets a specific stat
    public void resetStats(StatType type) {
        if (!type.isCalculated()) {
            getFileConfig().set(type.getPath(), 0);
            if (Databases.SQL_ENABLED.getBoolean()) saveAsynchronously();
        }
    }

    public String getStats(StatType type) {
        return getPlayerStats().get(type);
    }

    // Returns a player's KD by dividing kills and deaths
    public String getKD(UUID id) {
        int kills = getFileConfig().getInt(StatType.KILLS.getPath());
        int deaths = getFileConfig().getInt(StatType.DEATHS.getPath());
        return String.format("%.2f", Utils.divide(kills, deaths));
    }

    // Returns a player's accuracy by dividing shots and hits
    public String getAccuracy(UUID id) {
        int shots = getFileConfig().getInt(StatType.SHOTS.getPath());
        int hits = getFileConfig().getInt(StatType.HITS.getPath());
        return String.format("%d%s", (int) Math.round(Utils.divide(hits, shots)*100), "%");
    }

    // Saves player information to PlayerData file
    // Called when the player enters an arena
    public void savePlayerInformation() {
        String path = "Player-State.";
        Player player = Bukkit.getPlayer(uuid);
        ExperienceManager exp = new ExperienceManager(player);

        new PlayerLocation(this, player.getLocation());
        fileConfig.set(path + "Gamemode", player.getGameMode().toString());
        fileConfig.set(path + "Food", player.getFoodLevel());
        fileConfig.set(path + "Health", player.getHealth());
        fileConfig.set(path + "Health-Scale", player.getHealthScale());
        fileConfig.set(path + "Exp", exp.getCurrentExp());
        fileConfig.set(path + "Allow-Flight", player.getAllowFlight());
        fileConfig.set(path + "Flying", player.isFlying());
        fileConfig.set(path + "Inventory", Arrays.asList(player.getInventory().getContents()));
        fileConfig.set(path + "Armour", Arrays.asList(player.getInventory().getArmorContents()));

        this.saveFile();
        Utils.stripValues(player);
    }

    // Restores all of the player's settings, then sets the info to null
    public void restorePlayerInformation(boolean stripValues) {
        String path = "Player-State.";
        Player player = Bukkit.getPlayer(uuid);
        ExperienceManager exp = new ExperienceManager(player);

        if (stripValues)
            Utils.stripValues(player);

        player.teleport(new PlayerLocation(this).getLocation());
        player.setFoodLevel(fileConfig.getInt(path + "Food"));
        player.setGameMode(GameMode.valueOf(fileConfig.getString(path + "Gamemode")));
        player.setAllowFlight(fileConfig.getBoolean(path + "Allow-Flight"));
        player.setFlying(fileConfig.getBoolean(path + "Flying"));
        exp.setExp(fileConfig.getInt(path + "Exp"));
        double health = fileConfig.getDouble(path + "Health");
        double scale = fileConfig.getDouble(path + "Health-Scale");
        if (health > 20d || health < 0) {
            player.setHealth(20);
        } else {
            player.setHealth(health);
        }

        player.setHealthScale(scale);

        player.getInventory().setContents(getLastInventoryContents(path + "Inventory"));
        player.getInventory().setArmorContents(getLastInventoryContents(path + "Armour"));

        player.updateInventory();
        fileConfig.set("Player-State", null);
        this.saveFile();
    }

    // Increments the set path by one
    private void addOneToPath(String path) {
        getFileConfig().set(path, getFileConfig().getInt(path) + 1);
        if (Databases.SQL_ENABLED.getBoolean()) saveAsynchronously();
    }

    private ItemStack[] getLastInventoryContents(String path) {
        ItemStack[] items = new ItemStack[fileConfig.getList(path).size()];
        int count = 0;
        for (Object item : fileConfig.getList(path).toArray()) {
            if (item instanceof ItemStack) {
                items[count] = new ItemStack((ItemStack)item);
                count++;
            }
        }
        return items;
    }

    private void setValue(String path, Object object) {
        fileConfig.set(path, object);
    }
}
