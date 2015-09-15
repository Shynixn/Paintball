package me.synapz.paintball.storage;

import me.synapz.paintball.StatType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public final class Statistics extends PaintballFile {

    public static Statistics instance;

    public Statistics(Plugin pb) {
        super(pb, "stats.yml");
        instance = this;
    }

    public void addKill(UUID id) {
        checkIfToAdd(id);
        addOneToPath(StatType.KILLS.getPath(id));
        updateKillDeathRatio(id);

        saveFile();
    }

    public void addDeath(UUID id) {
        checkIfToAdd(id);
        addOneToPath(StatType.DEATHS.getPath(id));
        updateKillDeathRatio(id);

        saveFile();
    }

    public void setHighestKillStreak(UUID id, int killStreak) {
        if (getFileConfig().getInt(StatType.HIGEST_KILL_STREAK.getPath(id)) >= killStreak)
            return;

        getFileConfig().set(StatType.HIGEST_KILL_STREAK.getPath(id), killStreak);
        saveFile();
    }

    public void addGamesPlayed(UUID id, boolean won) {
        addOneToPath(StatType.GAMES_PLAYED.getPath(id));
        if (won) {
            addOneToPath(StatType.WON.getPath(id));
        } else {
            addOneToPath(StatType.LOST.getPath(id));
        }

        saveFile();
    }

    public String getPlayerAtRank(int rank, StatType type) {
        Map<String, Integer> uuidList = new HashMap<String, Integer>();

        for (String uuid : getFileConfig().getConfigurationSection("Stats").getKeys(false)) {
            uuidList.put(uuid, getFileConfig().getInt(type.getPath(uuid)));
        }
        List<Integer> ints = new ArrayList<Integer>();
        for (int i : uuidList.values()) {
            ints.add(i);
        }
        Collections.sort(ints);
        Collections.reverse(ints);
        if (ints.size() < rank) {
            return "No player found.";
        }
        for (String uuid : getFileConfig().getConfigurationSection("Stats").getKeys(false)) {
            int i = uuidList.get(uuid);
            if (ints.get(rank-1) == i) {
                if (Bukkit.getPlayer(uuid) == null) {
                    return Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
                } else {
                    return Bukkit.getPlayer(UUID.fromString(uuid)).getName();
                }
            }
        }
        return "No player found.";
    }

    private void checkIfToAdd(UUID id) {
        if (getFileConfig().getConfigurationSection("Stats." + id) == null) {
            // set the values to 0
            for (StatType value : StatType.values()) {
                getFileConfig().set(value.getPath(id), 0);
            }
        }
        saveFile();
    }

    private void addOneToPath(String path) {
        getFileConfig().set(path, getFileConfig().getInt(path) + 1);
    }

    private double updateKillDeathRatio(UUID id) {
        int kills = getFileConfig().getInt(StatType.KILLS.getPath(id));
        int deaths = getFileConfig().getInt(StatType.DEATHS.getPath(id));
        if (deaths == 0) {
            getFileConfig().set(StatType.KD.getPath(id), kills);
            saveFile();
            return kills;
        }
        if (kills == 0) {
            getFileConfig().set(StatType.KD.getPath(id), 0);
            saveFile();
            return 0;
        }
        float k = (float) kills;
        float d = (float) deaths;
        getFileConfig().set(StatType.KD.getPath(id), (k/d));
        saveFile();
        return (k/d);
    }

}
