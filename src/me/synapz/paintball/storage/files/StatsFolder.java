package me.synapz.paintball.storage.files;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

import static me.synapz.paintball.storage.Settings.SECONDARY;
import static me.synapz.paintball.storage.Settings.THEME;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.STRIKETHROUGH;

/**
 * Created by Jeremy Lugo on 7/24/2016.
 */
public class StatsFolder extends PaintballFile{

    private Map<UUID, UUIDStatsFile> files = new HashMap<>();

    public StatsFolder(Plugin plugin) {
        super(plugin, "stats");
    }

    public static void loadStatsFiles() {
        for (File file : Settings.getSettings().getStatsFolder().listFiles()) {
            UUID uuid = null;
            try {
                uuid = UUID.fromString(file.getName().replace(".yml", "").replace("/stats/", ""));
            } catch (IllegalArgumentException exc) {
                continue;
            }

            if (Settings.getSettings().getStatsFolder().getPlayerFile(uuid, false) != null) {
                // is already in list, continue
                continue;
            }

            new UUIDStatsFile(uuid);
        }
    }

    public UUIDStatsFile getPlayerFile(UUID uuid, boolean createIfNull) {
        UUIDStatsFile uuidStatsFile = files.get(uuid);

        if (createIfNull && uuidStatsFile == null)
            return new UUIDStatsFile(uuid);
        else
            return files.get(uuid);
    }

    public Collection<UUIDStatsFile> getUUIDStatsList() {
        return files.values();
    }

    public void addPlayerFile(UUIDStatsFile file) {
        while (files.containsValue(file.getUUID())) {
            files.remove(file.getUUID(), file);
        }

        files.put(file.getUUID(), file);
    }

    // Gets a page of stats returned by a list of strings
    public List<String> getPage(StatType statType, int page) {
        List<String> stats = new ArrayList<>();

        int end = page*10;
        int start = end-9;

        // Adds the title
        String title = statType == null ? new MessageBuilder(Messages.TOP_LEADERBOARD_TITLE).replace(Tag.PAGE, page + "").build() : new MessageBuilder(Messages.PER_LEADERBOARD_TITLE).replace(Tag.STAT, statType.getName().replace(" ", "")).replace(Tag.MAX, getMaxPage() + "").replace(Tag.PAGE, page + "").build();
        stats.add(title);

        // Starts adding the values to the stats list
        if (statType == null) {
            // Go through each value and find the rank of it and add it to the list
            for (StatType type : StatType.values()) {
                Multimap<String, UUID> playerAndStat = getPlayerAtRankMultimap(page, type);
                String value = playerAndStat.keySet().toArray()[0].toString();
                String playername = Bukkit.getOfflinePlayer((UUID) playerAndStat.values().toArray()[0]).getName();

                stats.add(new MessageBuilder(Messages.TOP_LEADERBOARD_LAYOUT)
                        .replace(Tag.RANK, page + "")
                        .replace(Tag.STAT, type.getName())
                        .replace(Tag.SENDER, playername)
                        .replace(Tag.AMOUNT, value)
                        .build());
            }
        } else {
            int add = 0;
            for (int i = start; i <= end; i++) {
                Multimap<String, UUID> playerAndStat = getPlayerAtRankMultimap(i, statType);
                for (UUID uuid : playerAndStat.values()) {
                    if (uuid == null) continue;

                    String playerName = Bukkit.getOfflinePlayer(uuid).getName();

                    String value = playerAndStat.keySet().toArray()[0].toString();
                    if (value.equals("Unknown")) continue;

                    String line = new MessageBuilder(Messages.PER_LEADERBOARD_LAYOUT)
                            .replace(Tag.RANK, (add + i) + "")
                            .replace(Tag.SENDER, playerName)
                            .replace(Tag.AMOUNT, value)
                            .build();

                    stats.add(line);
                    if (playerAndStat.values().size() > 1)
                        add++;
                }
                if (playerAndStat.values().size() > 1)
                    add--;
            }

            if (stats.size() > 10)
                stats = stats.subList(0, 9);
        }

        return stats;
    }

    // Gets a player at a rank, returns Unknown if no player can be found at rank
    public Multimap<String, UUID> getPlayerAtRankMultimap(int rank, StatType type) {
        Multimap<String, UUID> result = ArrayListMultimap.create();
        result.put("Unknown", UUID.randomUUID());

        Map<UUID, String> uuidList = new HashMap<>();

        for (UUIDStatsFile uuidStatsFile : Settings.getSettings().getStatsFolder().getUUIDStatsList()) {
            UUID uuid = uuidStatsFile.getUUID();
            uuidList.put(uuid, uuidStatsFile.getPlayerStats().get(type));
        }

        Multimap<Double, UUID> playersWithSameValue = ArrayListMultimap.create();
        List<Double> scores = new ArrayList<>();
        for (UUID player : uuidList.keySet()) {
            String stat = uuidList.get(player);
            if (stat == null)
                continue;

            stat = stat.replace("%", "");
            stat = stat.replace(",", ".");
            if (!scores.contains(Double.parseDouble(stat)))
                scores.add(Double.parseDouble(stat));
            playersWithSameValue.put(Double.parseDouble(stat), player);
        }

        Collections.sort(scores);
        Collections.reverse(scores);
        if (scores.size() < rank) {
            return result;//
        }

        // We have a value!
        result.clear();

        Double scoreToGet = (scores.get(rank - 1));
        result.putAll(String.valueOf(scoreToGet), playersWithSameValue.get(scoreToGet));

        return result;
    }

    public Map<String, String> getPlayerAtRankMap(int rank, StatType type) {
        HashMap<String, String> result = new HashMap<String, String>() {{
            put("Unknown", "");
        }};

        Map<String, String> uuidList = new HashMap<>();

        for (UUIDStatsFile uuidStatsFile : Settings.getSettings().getStatsFolder().getUUIDStatsList()) {
            String uuid = uuidStatsFile.getUUID().toString();
            uuidList.put(uuid, uuidStatsFile.getPlayerStats().get(type));
        }

        List<Double> statValues = new ArrayList<>();
        for (String stat : uuidList.values()) {
            if (stat == null)
                continue;

            stat = stat.replace("%", "");
            stat = stat.replace(",", ".");
            statValues.add(Double.parseDouble(stat));
        }

        Collections.sort(statValues);
        Collections.reverse(statValues);
        if (statValues.size() < rank) {
            return result;//
        }

        for (String uuid : uuidList.keySet()) {
            double value = Double.parseDouble(uuidList.get(uuid).replace("%", "").replace(",", "."));
            if (statValues.get(rank - 1) == value) {
                UUIDStatsFile uuidStatsFile = Settings.getSettings().getStatsFolder().getPlayerFile(UUID.fromString(uuid), true);

                result.clear(); // remove all entries so we know there will only be 1 set of things returning
                if (Bukkit.getServer().getPlayer(UUID.fromString(uuid)) == null) {
                    String name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
                    String score = uuidStatsFile.getPlayerStats().get(type);
                    result.put(name == null ? "Unknown" : name, score == null ? "" : score);
                    return result;
                } else {
                    String name = Bukkit.getPlayer(UUID.fromString(uuid)).getName();
                    String score = uuidStatsFile.getPlayerStats().get(type);
                    result.put(name == null ? "Unknown" : name, score == null ? "" : score);
                    return result;
                }
            }
        }
        return result;
    }

    public void getStats(Player sender, String targetName) {
        UUID target = Bukkit.getPlayer(targetName) == null ? Bukkit.getOfflinePlayer(targetName).getUniqueId() : Bukkit.getPlayer(targetName).getUniqueId();

        UUIDStatsFile file = getPlayerFile(target, false);

        if (file == null) {
            Messenger.error(sender, "Could not find player's stats.");
            return;
        }

        Map<StatType, String> stats = file.getPlayerStats();

        Messenger.msg(sender, SECONDARY + STRIKETHROUGH + "             " + RESET + " " + THEME + Bukkit.getOfflinePlayer(target).getName() + "'s Stats" + RESET + " " + SECONDARY + STRIKETHROUGH + "             ");

        for (StatType type : StatType.values()) {
            String name = type.getName();
            if (type == StatType.SHOTS || type == StatType.HITS || type == StatType.KILLS || type == StatType.DEATHS || type == StatType.DEFEATS || type == StatType.WINS)
                name = "  " + name;
            Messenger.msg(sender, THEME + name + ": " + SECONDARY + stats.get(type));
        }
    }

    public int getMaxPage() {
        int listSize = this.files.values().size();

        if (listSize > 0 && listSize <= 10)
            return 1;
        return (listSize/10)%10 == 0 ? listSize/10 : (listSize/10)+1;
    }
}
