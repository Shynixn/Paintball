package me.synapz.paintball.storage;


import me.synapz.paintball.ExperienceManager;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.Utils;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.*;
import java.util.*;

import static me.synapz.paintball.storage.Settings.*;

public final class PlayerData extends PaintballFile {

    private Map<String, Location> locations = new HashMap<>();
    private Map<String, GameMode> gamemodes = new HashMap<>();
    private Map<String, Integer> foodLevels = new HashMap<>();
    private Map<String, Double> health = new HashMap<>();
    private Map<String, ItemStack[]> inventories = new HashMap<>();
    private Map<String, ItemStack[]> armour = new HashMap<>();
    private Map<String, Integer> expLevels = new HashMap<>();
    private Map<String, Scoreboard> scoreboards = new HashMap<>();
    private Map<String, Boolean> flying = new HashMap<>();
    private Map<String, Collection<PotionEffect>> potions = new HashMap<>();

    public PlayerData(Plugin pb) {
        super(pb, "playerdata.yml");
    }

    @Override
    public FileConfiguration getFileConfig() {
        if (SQL) {
            return this.addStats(fileConfig);
        }
        return this.fileConfig;
    }

    @Override
    public void saveFile() {
        if (SQL) {
            try {
                this.removeStats(fileConfig).save(this);
            }catch (Exception exc) {
                Messenger.error(Bukkit.getConsoleSender(), "Error saving SQL. Check config.yml's SQL settings. Falling back to playerdata.yml's stats.");
                exc.printStackTrace();
            }
        } else {
            super.saveFile();
        }
    }

    public FileConfiguration removeStats(FileConfiguration yaml) {
        Set<String> keys = yaml.getConfigurationSection("Player-Data").getKeys(false);
        YamlConfiguration statsYaml = new YamlConfiguration();
        for (String key : keys) {
            ConfigurationSection stats = yaml.getConfigurationSection(key + ".Stats");
            String path = stats.getCurrentPath();
            statsYaml.set(path, stats);
            yaml.set(path, null);
        }
        byte[] byteArray = statsYaml.saveToString().getBytes();
        String encoded = Base64.getEncoder().encode(byteArray).toString();
        yaml.set("Stats", encoded);
        Utils.executeQuery("INSERT INTO Paintball_Stats (id,stats) VALUES (1," + encoded + ")");
        return yaml;
    }


    // Adds one to a player's stat
    // ex: if a player gets 1 kill, add one the stat in config
    public void incrementStat(StatType type, ArenaPlayer player) {
        UUID id = player.getPlayer().getUniqueId();

        switch (type) {
            // KD and ACCURACY are automatically determined by dividing
            case KD:
                return;
            case ACCURACY:
                return;
            case HIGEST_KILL_STREAK:
                // killstreak is less than past killstreak, return
                if (getFileConfig().getInt(StatType.HIGEST_KILL_STREAK.getPath(id)) >= player.getKillStreak())
                    return;
                getFileConfig().set(StatType.HIGEST_KILL_STREAK.getPath(id), player.getKillStreak());
                return;
            case GAMES_PLAYED:
                if (player.isWinner())
                    addOneToPath(StatType.WINS.getPath(id));
                else
                    addOneToPath(StatType.DEFEATS.getPath(id));
                break; // not return; because it still has to increment the games played
        }

        addOneToPath(type.getPath(id));
        saveFile();
    }

    // Gets a player at a rank, returns Unknown if no player can be found at rank
    public HashMap<String, String> getPlayerAtRank(int rank, StatType type) {
        HashMap<String, String> result = new HashMap<String, String>() {{
            put("Unknown", "");
        }};

        Map<String, String> uuidList = new HashMap<String, String>();
        // TODO check if null!
        for (String uuid : getFileConfig().getConfigurationSection("Player-Data").getKeys(false)) {
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
        boolean uuidNotFound = getFileConfig().getConfigurationSection("Player-Data." + target + ".Stats") == null;

        for (StatType type : StatType.values()) {
            if (uuidNotFound) // their uuid wasn't in file so they have no stats, so add 0 for everything
                stats.put(type, 0 + "");
            else
                stats.put(type, type == StatType.KD ? getKD(target) : type == StatType.ACCURACY ? getAccuracy(target) : getFileConfig().getString(type.getPath(target)));
        }
        return stats;
    }

    // Method for /pb leaderboard <stat> <page>
    // Organizes players from best to worse based on the stat selected
    // TODO: does not look nice, and doesn't have enough players to test fully
    public void paginate(CommandSender sender, StatType type, int page, int pageLength) {
        SortedMap<String, String> allPlayers = new TreeMap<String, String>(Collections.<String>reverseOrder());
        for (String uuid : getFileConfig().getConfigurationSection("Player-Data").getKeys(false)) {
            String name;
            String score;
            if (Bukkit.getServer().getPlayer(UUID.fromString(uuid)) == null) {
                name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
                score = getPlayerStats(UUID.fromString(uuid)).get(type);
            } else {
                name = Bukkit.getPlayer(UUID.fromString(uuid)).getName();
                score = getPlayerStats(UUID.fromString(uuid)).get(type);
            }
            allPlayers.put(name, score);
        }
        sender.sendMessage(ChatColor.YELLOW + "List: Page (" + String.valueOf(page) + " of " + (((allPlayers.size() % pageLength) == 0) ? allPlayers.size() / pageLength : (allPlayers.size() / pageLength) + 1));
        int i = 0, k = 0;
        page--;
        for (final Map.Entry<String, String> e : allPlayers.entrySet()) {
            k++;
            if ((((page * pageLength) + i + 1) == k) && (k != ((page * pageLength) + pageLength + 1))) {
                i++;
                sender.sendMessage(ChatColor.YELLOW + e.getKey() + " - " + e.getValue());
            }
        }
    }

    // Gets the page of a player's stats, useful for /pb stat and /pb leaderboard <page>
    // TODO: not working at all
    public void getPage(Player player, StatType type, int page) {
        // int totalPages = getFileConfig().getConfigurationSection("Player-Data").getKeys(false).size() % 8;
        int current = Integer.parseInt(page + "" + page);
        int end = current + 8;
        // TODO: 11/20/15  add prefix
        for (String uuid : getFileConfig().getConfigurationSection("Player-Data").getKeys(false)) {
            Messenger.info(player, "#" + current + " - " + Bukkit.getPlayer(UUID.fromString(uuid)) == null ? Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() : Bukkit.getPlayer(UUID.fromString(uuid)).getName() + " Many: " + getPlayerStats(UUID.fromString(uuid)).get(type));
        }
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

    private FileConfiguration addStats(FileConfiguration yaml) {
        YamlConfiguration statsYaml = new YamlConfiguration();
        try {
            Connection conn;
            conn = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
            PreparedStatement sql = conn.prepareStatement("SELECT statsFROM `Paintball_Stats` WHERE id = '1';");
            ResultSet result = sql.executeQuery();
            result.next();
            String base64Stats = result.getString("stats");
            String yamlString = Base64.getDecoder().decode(base64Stats.getBytes()).toString();
            statsYaml.loadFromString(yamlString);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().info("SQL connection failed! Using offline backup until we can connect again");
            if (yaml.contains("Stats")) {
                String base64Stats = yaml.getString("Stats");
                String yamlString = Base64.getDecoder().decode(base64Stats.getBytes()).toString();
                try {
                    statsYaml.loadFromString(yamlString);
                } catch (InvalidConfigurationException e1) {
                    e1.printStackTrace();
                    Bukkit.getLogger().severe("Failed to load offline config! Please check SQL connection and playerdata file!");
                }
            } else {
                Bukkit.getLogger().severe("Statistics Down!! We have no SQL connection and don't have a backup of stats!");
            }
        }
        Set<String> keys = statsYaml.getConfigurationSection("Player-Data").getKeys(false);
        for (String key : keys) {
            ConfigurationSection stats = statsYaml.getConfigurationSection(key + ".Stats");
            String path = stats.getCurrentPath();
            yaml.set(path, stats);
        }
        return yaml;
    }

    // Saves player information to PlayerData file
    // Called when the player enters an arena
    // TODO: add exp and other missing values
    public void savePlayerInformation(Player player) {
        ExperienceManager exp = new ExperienceManager(player);
        String id = player.getName();
        locations.put(id, player.getLocation());
        gamemodes.put(id, player.getGameMode());
        foodLevels.put(id, player.getFoodLevel());
        health.put(id, player.getHealth());
        inventories.put(id, player.getInventory().getContents());
        armour.put(id, player.getInventory().getArmorContents());
        expLevels.put(id, exp.getCurrentExp());
        scoreboards.put(id, player.getScoreboard() == null ? Bukkit.getScoreboardManager().getNewScoreboard() : player.getScoreboard());
        flying.put(id, player.isFlying() && player.getAllowFlight());
        potions.put(id, player.getActivePotionEffects());

        addStatsIfNotYetAdded(player.getUniqueId());
        Utils.stripValues(player);
        saveFile();
    }

    // Restores all of the player's settings, then sets the info to null
    public void restorePlayerInformation(Player player) {
        Utils.stripValues(player);
        ExperienceManager exp = new ExperienceManager(player);
        String id = player.getName();

        player.teleport(locations.get(id));
        player.getInventory().setContents(inventories.get(id));
        player.getInventory().setArmorContents(armour.get(id));
        player.setFoodLevel(foodLevels.get(id));
        player.setHealth(health.get(id));
        player.setGameMode(gamemodes.get(id));
        player.setScoreboard(scoreboards.get(id));
        player.setAllowFlight(flying.get(id));
        player.setFlying(flying.get(id));
        exp.setExp(expLevels.get(id));
        player.addPotionEffects(potions.get(id));

        locations.remove(id);
        gamemodes.remove(id);
        foodLevels.remove(id);
        health.remove(id);
        inventories.remove(id);
        expLevels.remove(id);
        scoreboards.remove(id);
        flying.remove(id);
    }

    // Checks to make sure there is a configuration section, if it isn't it is created for that player
    // Checks to see if there are any missing stats from config, this would happen in a future upgrade and it sets that stat to 0 isntead of it being null
    private void addStatsIfNotYetAdded(UUID id) {
        // checks to make sure the stat's are in config, if not make it
        if (getFileConfig().getConfigurationSection("Player-Data." + id + ".Stats") == null) {
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
        saveFile();
    }

    // Increments the set path by one
    private void addOneToPath(String path) {
        // TODO: file saves but only updates Stat values on reload :(
        getFileConfig().set(path, getFileConfig().getInt(path) + 1);
    }
}
