package me.synapz.paintball.storage;

import me.synapz.paintball.Message;
import me.synapz.paintball.PbPlayer;
import me.synapz.paintball.StatType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.generator.InternalChunkGenerator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.util.*;

public final class Statistics extends PaintballFile {

    public static Statistics instance;

    public Statistics(Plugin pb) {
        super(pb, "stats.yml");
        instance = this;
    }

    public void incrimentStat(StatType type, PbPlayer player) {
        UUID id = player.getPlayer().getUniqueId();
        check(id);

        if (type == StatType.HIGEST_KILL_STREAK) {
            // killstreak is less than past killstreak, return
            if (getFileConfig().getInt(StatType.HIGEST_KILL_STREAK.getPath(id)) >= player.getKillstreak())
                return;

            getFileConfig().set(StatType.HIGEST_KILL_STREAK.getPath(id), player.getKillstreak());
            return;
        } else if (type == StatType.GAMES_PLAYED) {
            if (player.ifWon()) {
                addOneToPath(StatType.WINS.getPath(id));
            } else {
                addOneToPath(StatType.DEATHS.getPath(id));
            }
        } else if (type == StatType.SHOTS) {
            // todo:
            // if hit() incriemnthit
        }
        addOneToPath(type.getPath(id));
        saveFile();
    }

    public void incrimentStat(StatType type, Player player) {
        UUID id = player.getPlayer().getUniqueId();
        check(id);
        addOneToPath(type.getPath(id));
        saveFile();
    }

    public HashMap<String, String> getPlayerAtRank(int rank, StatType type) {
        HashMap<String, String> result = new HashMap<String, String>(){{
            put("Unknown", "");
        }};

        Map<String, String> uuidList = new HashMap<String, String>();
        // TODO check if null!
        for (String uuid : getFileConfig().getConfigurationSection("Stats").getKeys(false)) {
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
        boolean uuidNotFound = getFileConfig().getConfigurationSection("Stats." + target) == null;

        for (StatType type : StatType.values()) {
            if (uuidNotFound) // their uuid wasn't in file so they have no stats, so add 0 for everything
                stats.put(type, 0+"");
            else
                stats.put(type, type == StatType.KD ? getKD(target) : type == StatType.ACCURACY ? getAccuracy(target) : getFileConfig().getString(type.getPath(target)));
        }
        return stats;
    }

    public void getPage(Player player, StatType type, int page) {
        // TODO: remove
        for (StatType t : StatType.values()) {
            incrimentStat(type, player);
        }
        // int totalPages = getFileConfig().getConfigurationSection("Stats").getKeys(false).size() % 8;
        int current = Integer.parseInt(page+""+page);
        int end = current+8;
        // TODO: 11/20/15  add prefix
        for (String uuid : getFileConfig().getConfigurationSection("Stats").getKeys(false)) {
            Message.getMessenger().msg(player, false, false, "#" + current + " - " + Bukkit.getPlayer(UUID.fromString(uuid)) == null ? Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() : Bukkit.getPlayer(UUID.fromString(uuid)).getName() + " Many: " + getPlayerStats(UUID.fromString(uuid)).get(type));
        }
    }

    public String getKD(UUID id) {
        check(id);
        int kills = getFileConfig().getInt(StatType.KILLS.getPath(id));
        int deaths = getFileConfig().getInt(StatType.DEATHS.getPath(id));
        return divide(kills, deaths);
    }

    public String getAccuracy(UUID id) {
        // todo: does this even work?
        check(id);
        int shots = getFileConfig().getInt(StatType.SHOTS.getPath(id));
        int hits = getFileConfig().getInt(StatType.HITS.getPath(id));
        return divide(shots, hits);
    }

    private String divide(int numerator, int denominator) {
        if (numerator == 0)
            return 0+"";
        if (denominator == 0)
            return numerator+"";

        float n = (float) numerator;
        float d = (float) denominator;
        return String.format("%.2f", (n/d));
    }

    private void check(UUID id) {
        // checks to make sure the stat's are in config, if not make it
        if (getFileConfig().getConfigurationSection("Stats." + id) == null) {
            // set the values to 0
            for (StatType value : StatType.values()) {
                getFileConfig().set(value.getPath(id), 0);
            }
        }
        // checks to see if their stats path is missing for a stat, useful for future upgrades with new stats
        for (StatType type : StatType.values()) {
            if (getFileConfig().getString(type.getPath(id)) == null) {
                getFileConfig().set(type.getPath(id), 0);
            }
        }
        saveFile();
    }

    private void addOneToPath(String path) {
        getFileConfig().set(path, getFileConfig().getInt(path) + 1);
    }

}
