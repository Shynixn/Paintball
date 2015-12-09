package me.synapz.paintball.storage;


import me.synapz.paintball.*;
import me.synapz.paintball.enums.StatType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public final class PlayerData extends PaintballFile {

    FileConfiguration data;

    public PlayerData(Plugin pb) {
        super(pb, "playerdata.yml");

        this.data = getFileConfig();
    }

    public void incrementStat(StatType type, PbPlayer player) {
        UUID id = player.getPlayer().getUniqueId();

        switch (type) {
            case HIGEST_KILL_STREAK:
                // killstreak is less than past killstreak, return
                if (getFileConfig().getInt(StatType.HIGEST_KILL_STREAK.getPath(id)) >= player.getKillstreak())
                    return;

                getFileConfig().set(StatType.HIGEST_KILL_STREAK.getPath(id), player.getKillstreak());
                return;
            case GAMES_PLAYED:
                if (player.ifWon()) {
                    addOneToPath(StatType.WINS.getPath(id));
                } else {
                    addOneToPath(StatType.DEFEATS.getPath(id));
                }
                break;
            case SHOTS:
                // todo:
                // if hit() incriemnthit
        }

        addOneToPath(type.getPath(id));
        saveFile();
    }

    public void incrementStat(StatType type, Player player) {
        UUID id = player.getPlayer().getUniqueId();
        addOneToPath(type.getPath(id));
        saveFile();
    }

    public HashMap<String, String> getPlayerAtRank(int rank, StatType type) {
        HashMap<String, String> result = new HashMap<String, String>(){{
            put("Unknown", "");
        }};

        Map<String, String> uuidList = new HashMap<String, String>();
        // TODO check if null!
        for (String uuid : getFileConfig().getConfigurationSection("Player-Data").getKeys(false)) {
            uuidList.put(uuid, getPlayerStats(UUID.fromString(uuid)).get(type));
        }

        List<Double> statValues = new ArrayList<Double>();
        for (String stat : uuidList.values()) {
            statValues.add(Double.parseDouble(stat));
        }
        Collections.sort(statValues);
        Collections.reverse(statValues);
        if (statValues.size() < rank) {
            return result;
        }
        for (String uuid : uuidList.keySet()) {
            double value = Double.parseDouble(uuidList.get(uuid));
            if (statValues.get(rank-1) == value) {
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

    public Map<StatType, String> getPlayerStats(UUID target) {
        Map<StatType, String> stats = new HashMap<StatType, String>();
        boolean uuidNotFound = getFileConfig().getConfigurationSection("Player-Data." + target + ".Stats") == null;

        for (StatType type : StatType.values()) {
            if (uuidNotFound) // their uuid wasn't in file so they have no stats, so add 0 for everything
                stats.put(type, 0+"");
            else
                stats.put(type, type == StatType.KD ? getKD(target) : type == StatType.ACCURACY ? getAccuracy(target) : getFileConfig().getString(type.getPath(target)));
        }
        return stats;
    }

    public  void paginate(CommandSender sender, StatType type, int page, int pageLength) {
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

    public void getPage(Player player, StatType type, int page) {
        // int totalPages = getFileConfig().getConfigurationSection("Player-Data").getKeys(false).size() % 8;
        int current = Integer.parseInt(page+""+page);
        int end = current+8;
        // TODO: 11/20/15  add prefix
        for (String uuid : getFileConfig().getConfigurationSection("Player-Data").getKeys(false)) {
            Message.getMessenger().msg(player, false, false, "#" + current + " - " + Bukkit.getPlayer(UUID.fromString(uuid)) == null ? Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() : Bukkit.getPlayer(UUID.fromString(uuid)).getName() + " Many: " + getPlayerStats(UUID.fromString(uuid)).get(type));
        }
    }

    public String getKD(UUID id) {
        int kills = getFileConfig().getInt(StatType.KILLS.getPath(id));
        int deaths = getFileConfig().getInt(StatType.DEATHS.getPath(id));
        return String.format("%.2f", divide(kills, deaths));
    }

    public String getAccuracy(UUID id) {
        int shots = getFileConfig().getInt(StatType.SHOTS.getPath(id));
        int hits = getFileConfig().getInt(StatType.HITS.getPath(id));
        return String.format("%d%s", (int) divide(shots, hits), "%");
    }

    private double divide(int numerator, int denominator) {
        if (numerator == 0)
            return 0;
        if (denominator == 0)
            return numerator;

        float n = (float) numerator;
        float d = (float) denominator;
        return (n/d);
    }

    public void savePlayerInformation(Player player) {
        UUID id = player.getUniqueId();
        data.set("Player-Data." + id + ".Name", player.getName());
        data.set("Player-Data." + id + ".Location", player.getLocation());
        data.set("Player-Data." + id + ".GameMode", player.getGameMode().getValue());
        data.set("Player-Data." + id + ".FoodLevel", player.getFoodLevel());
        data.set("Player-Data." + id + ".Health", player.getHealth());
        data.set("Player-Data." + id + ".Inventory", Utils.getInventoryList(player, false));
        data.set("Player-Data." + id + ".Armour", Utils.getInventoryList(player, true));
        addStatsIfNotYetAdded(id);

        saveFile();
    }

    public void restorePlayerInformation(UUID id) {
        Player player = Bukkit.getServer().getPlayer(id);

        player.teleport((Location) data.get("Player-Data." + id + ".Location"));
        player.getInventory().setContents(getLastInventoryContents(id, ".Inventory"));
        player.getInventory().setArmorContents(getLastInventoryContents(id, ".Armour"));
        player.setFoodLevel(data.getInt("Player-Data." + id + ".FoodLevel"));
        player.setHealth(data.getInt("Player-Data." + id + ".Health"));
        player.setGameMode(Utils.getLastGameMode(data.getInt("Player-Data." + id + ".GameMode")));

        data.set(id.toString(), null);
        saveFile();
    }

    private ItemStack[] getLastInventoryContents(UUID id, String path) {
        ItemStack[] items = new ItemStack[data.getList("Player-Data." + id + path).size()];
        int count = 0;
        for (Object item : data.getList("Player-Data." + id + path).toArray()) {
            if (item instanceof ItemStack) {
                items[count] = new ItemStack((ItemStack)item);
                count++;
            }
        }
        return items;
    }

    private void addStatsIfNotYetAdded(UUID id) {
        // checks to make sure the stat's are in config, if not make it
        if (getFileConfig().getConfigurationSection("Player-Data." + id + "Stats") == null) {
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

    private void addOneToPath(String path) {
        getFileConfig().set(path, getFileConfig().getInt(path) + 1);
    }
}
