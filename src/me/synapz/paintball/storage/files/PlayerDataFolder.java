package me.synapz.paintball.storage.files;

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

public class PlayerDataFolder extends PaintballFile {

    private Map<UUID, UUIDFile> files = new HashMap<>();

    public PlayerDataFolder(Plugin plugin) {
        super(plugin, "playerdata");
    }

    public static void loadPlayerDataFiles() {
        for (File file : Settings.getSettings().getPlayerDataFolder().listFiles()) {
            UUID uuid = null;
            try {
                uuid = UUID.fromString(file.getName().replace(".yml", "").replace("/playerdata/", ""));
            } catch (IllegalArgumentException exc) {
                continue;
            }
            new UUIDFile(uuid);
        }
    }

    public UUIDFile getPlayerFile(UUID uuid) {
        return files.get(uuid);
    }

    public Collection<UUIDFile> getPlayerDataList() {
        return files.values();
    }

    public void addPlayerFile(UUIDFile file) {
        while (files.containsValue(file.getUUID())) {
            files.remove(file.getUUID(), file);
        }

        files.put(file.getUUID(), file);
    }

    // Gets a page of stats returned by a list of strings
    public List<String> getPage(StatType statType, int page) {
        List<String> stats = new ArrayList<>();

        int end = page*10;
        int start = end-10;

        // Adds the title
        String title = statType == null ? new MessageBuilder(Messages.TOP_LEADERBOARD_TITLE).replace(Tag.PAGE, page + "").build() : new MessageBuilder(Messages.PER_LEADERBOARD_TITLE).replace(Tag.STAT, statType.getName().replace(" ", "")).replace(Tag.MAX, getMaxPage() + "").replace(Tag.PAGE, page + "").build();
        stats.add(title);

        // Starts adding the values to the stats list
        if (statType == null) {
            // Go through each value and find the rank of it and add it to the list
            for (StatType type : StatType.values()) {
                Map<String, String> playerAndStat = getPlayerAtRank(page, type);
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
                    Map<String, String> playerAndStat = getPlayerAtRank(i, statType);
                    String playerName = (String) playerAndStat.keySet().toArray()[0];

                    if (!playerName.equals("Unknown")) {
                        String value = playerAndStat.values().toArray()[0].toString();

                        String line = new MessageBuilder(Messages.PER_LEADERBOARD_LAYOUT)
                                .replace(Tag.RANK, i + "")
                                .replace(Tag.SENDER, playerName)
                                .replace(Tag.AMOUNT, value)
                                .build();

                        if (!stats.contains(line))
                            stats.add(line);
                    }
                }
            }
        }

        return stats;
    }

    // Gets a player at a rank, returns Unknown if no player can be found at rank
    public Map<String, String> getPlayerAtRank(int rank, StatType type) {
        HashMap<String, String> result = new HashMap<String, String>() {{
            put("Unknown", "");
        }};

        Map<String, String> uuidList = new HashMap<String, String>();

        for (UUIDFile uuidFile : Settings.getSettings().getPlayerDataFolder().getPlayerDataList()) {
            String uuid = uuidFile.getUUID().toString();
            uuidList.put(uuid, uuidFile.getPlayerStats().get(type));
        }

        List<Double> statValues = new ArrayList<Double>();
        for (String stat : uuidList.values()) {
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
            double value = Double.parseDouble(uuidList.get(uuid).replace("%", ""));
            if (statValues.get(rank - 1) == value) {
                UUIDFile uuidFile = Settings.getSettings().getPlayerDataFolder().getPlayerFile(UUID.fromString(uuid));

                result.clear(); // remove all entries so we know there will only be 1 se// t of things returning
                if (Bukkit.getServer().getPlayer(UUID.fromString(uuid)) == null) {
                    String name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
                    String score = uuidFile.getPlayerStats().get(type);
                    result.put(name == null ? "Unknown" : name, score == null ? "" : score);
                    return result;
                } else {
                    String name = Bukkit.getPlayer(UUID.fromString(uuid)).getName();
                    String score = uuidFile.getPlayerStats().get(type);
                    result.put(name == null ? "Unknown" : name, score == null ? "" : score);
                    return result;
                }
            }
        }
        return result;
    }

    public void getStats(Player sender, String targetName) {
        UUID target = Bukkit.getPlayer(targetName) == null ? Bukkit.getOfflinePlayer(targetName).getUniqueId() : Bukkit.getPlayer(targetName).getUniqueId();

        UUIDFile file = getPlayerFile(target);

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