package me.synapz.paintball.storage.files;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.enums.Databases;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.storage.database.Database;
import me.synapz.paintball.utils.ExperienceManager;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.SQLException;
import java.util.*;

import static me.synapz.paintball.storage.Settings.SECONDARY;
import static me.synapz.paintball.storage.Settings.THEME;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.STRIKETHROUGH;

public class PlayerDataFile extends PaintballFile {

    private Map<String, Location> locations = new HashMap<>();
    private Map<String, GameMode> gamemodes = new HashMap<>();
    private Map<String, Integer> foodLevels = new HashMap<>();
    private Map<String, Double> health = new HashMap<>();
    private Map<String, ItemStack[]> inventories = new HashMap<>();
    private Map<String, ItemStack[]> armour = new HashMap<>();
    private Map<String, Integer> expLevels = new HashMap<>();
    private Map<String, Scoreboard> scoreboards = new HashMap<>();
    private Map<String, Boolean> flying = new HashMap<>();
    private Map<String, Boolean> allowFly = new HashMap<>();
    private Map<String, Collection<PotionEffect>> potions = new HashMap<>();

    private Database database;

    public PlayerDataFile(Plugin pb) {
        super(pb, "playerdata.yml");
    }

    @Override
    public FileConfiguration getFileConfig() {
        return this.fileConfig;
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

    // Gets a player at a rank, returns Unknown if no player can be found at rank
    public Map<String, String> getPlayerAtRank(int rank, StatType type) {
        HashMap<String, String> result = new HashMap<String, String>() {{
            put("Unknown", "");
        }};

        Map<String, String> uuidList = new HashMap<String, String>();

        for (String uuid : getSection("Player-Data").getKeys(false)) {
            addStatsIfNotYetAdded(UUID.fromString(uuid));
            uuidList.put(uuid, getPlayerStats(UUID.fromString(uuid)).get(type));
        }

        List<Double> statValues = new ArrayList<Double>();
        for (String stat : uuidList.values()) {
            stat = stat.replace("%", "");
            statValues.add(Double.parseDouble(stat));
        }
        Collections.sort(statValues);
        Collections.reverse(statValues);
        if (statValues.size() < rank) {
            return result;
        }
        for (String uuid : uuidList.keySet()) {
            double value = Double.parseDouble(uuidList.get(uuid).replace("%", ""));
            if (statValues.get(rank - 1) == value) {
                result.clear(); // remove all entries so we know there will only be 1 set of things returning
                if (Bukkit.getServer().getPlayer(UUID.fromString(uuid)) == null) {
                    String name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
                    String score = getPlayerStats(UUID.fromString(uuid)).get(type);
                    result.put(name == null ? "Unknown" : name, score == null ? "" : score);
                    return result;
                } else {
                    String name = Bukkit.getPlayer(UUID.fromString(uuid)).getName();
                    String score = getPlayerStats(UUID.fromString(uuid)).get(type);
                    result.put(name == null ? "Unknown" : name, score == null ? "" : score);
                    return result;
                }
            }
        }
        return result;
    }

    // Returns a player's stats in a Map with StatType holding the type connected to a String with it's value
    // Usedful for leaderboards and /pb stats
    public Map<StatType, String> getPlayerStats(UUID target) {
        Map<StatType, String> stats = new HashMap<StatType, String>();
        boolean uuidNotFound = getFileConfig().getConfigurationSection("Player-Data." + target) == null;

        for (StatType type : StatType.values()) {
            if (uuidNotFound) // their uuid wasn't in file so they have no stats, so add 0 for everything
                stats.put(type, 0 + "");
            else
                stats.put(type, type == StatType.KD ? getKD(target) : type == StatType.ACCURACY ? getAccuracy(target) : getFileConfig().getString(type.getPath(target)));
        }
        return stats;
    }

    public String getStats(UUID target, StatType type) {
        return getPlayerStats(target).get(type);
    }

    public int getMaxPage() {
        int listSize = getSection("Player-Data").getKeys(false).size();

        if (listSize > 0 && listSize <= 10)
            return 1;
        return (listSize/10)%10 == 0 ? listSize/10 : (listSize/10)+1;
    }

    // Gets a page of stats returned by a list of strings
    public List<String> getPage(StatType statType, int page) {
        List<String> stats = new ArrayList<>();

        int end = page*10;
        int start = end-10;

        // Adds the title
        String title = statType == null ? new MessageBuilder(Messages.TOP_LEADERBOARD_TITLE).replace(Tag.PAGE, page + "").build() : new MessageBuilder(Messages.PER_LEADERBOARD_TITLE).replace(Tag.STAT, statType.getName()).replace(Tag.MAX, getMaxPage() + "").replace(Tag.PAGE, page + "").build();
        stats.add(title);

        // Starts adding the values to the stats list
        if (statType == null) {
            // Go through each value and find the rank of it and add it to the list
            for (StatType type : StatType.values()) {
                Map<String, String> playerAndStat = Settings.PLAYERDATA.getPlayerAtRank(page, type);
                String value = playerAndStat.values().toArray()[0].toString();

                stats.add(new MessageBuilder(Messages.TOP_LEADERBOARD_LAYOUT)
                        .replace(Tag.RANK, page + "")
                        .replace(Tag.STAT, type.getName())
                        .replace(Tag.SENDER, playerAndStat.keySet().toArray()[0] + "")
                        .replace(Tag.AMOUNT, value)
                        .build());
            }
        } else {
            for (int i = start; i <= end; i++) {
                if (i > 0) {
                    Map<String, String> playerAndStat = Settings.PLAYERDATA.getPlayerAtRank(i, statType);
                    String playerName = (String) playerAndStat.keySet().toArray()[0];

                    if (!playerName.equals("Unknown")) {
                        String value = playerAndStat.values().toArray()[0].toString();

                        stats.add(new MessageBuilder(Messages.PER_LEADERBOARD_LAYOUT)
                                .replace(Tag.RANK, i + "")
                                .replace(Tag.SENDER, playerName)
                                .replace(Tag.AMOUNT, value)
                                .build());
                    }
                }
            }
        }

        return stats;
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
        allowFly.put(id,  player.getAllowFlight());
        flying.put(id, player.isFlying());
        potions.put(id, player.getActivePotionEffects());

        addStatsIfNotYetAdded(player.getUniqueId());
        Utils.stripValues(player);
    }

    public void getStats(Player sender, String targetName) {
        UUID target = Bukkit.getPlayer(targetName) == null ? Bukkit.getOfflinePlayer(targetName).getUniqueId() : Bukkit.getPlayer(targetName).getUniqueId();

        Map<StatType, String> stats = Settings.PLAYERDATA.getPlayerStats(target);
        Messenger.msg(sender, SECONDARY + STRIKETHROUGH + "             " + RESET + " " + THEME + Bukkit.getOfflinePlayer(target).getName() + "'s Stats" + RESET + " " + SECONDARY + STRIKETHROUGH + "             ");

        for (StatType type : StatType.values()) {
            String name = type.getName();
            if (type == StatType.SHOTS || type == StatType.HITS || type == StatType.KILLS || type == StatType.DEATHS || type == StatType.DEFEATS || type == StatType.WINS)
                name = "  " + name;
            Messenger.msg(sender, THEME + name + ": " + SECONDARY + stats.get(type));
        }
    }

    // Restores all of the player's settings, then sets the info to null
    public void restorePlayerInformation(Player player) {
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

        locations.remove(id);
        gamemodes.remove(id);
        foodLevels.remove(id);
        health.remove(id);
        inventories.remove(id);
        expLevels.remove(id);
        scoreboards.remove(id);
        flying.remove(id);
        player.updateInventory();
    }

    // Checks to make sure there is a configuration section, if it isn't it is created for that player
    // Checks to see if there are any missing stats from config, this would happen in a future upgrade and it sets that stat to 0 isntead of it being null
    private void addStatsIfNotYetAdded(UUID id) {
        // checks to make sure the stat's are in config, if not make it
        if (getFileConfig().getConfigurationSection("Player-Data." + id) == null) {
            // set the values to 0
            for (StatType value : StatType.values()) {
                if (!value.isCalculated())
                    getFileConfig().set(value.getPath(id), 0);
            }
        }
        // checks to see if their stats path is missing for a stat, useful for future upgrades with new stats
        for (StatType type : StatType.values()) {
            if (!type.isCalculated() && getFileConfig().getString(type.getPath(id)) == null)
                getFileConfig().set(type.getPath(id), 0);
        }
    }

    private ConfigurationSection getSection(String path) {
        if (getFileConfig().getConfigurationSection(path) == null) {
            return getFileConfig().createSection(path);
        } else {
            return getFileConfig().getConfigurationSection(path);
        }
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
