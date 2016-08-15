package me.synapz.paintball.listeners;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.storage.files.StatsFolder;
import me.synapz.paintball.storage.files.UUIDStatsFile;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceholderRequest extends EZPlaceholderHook {

    private final Plugin plugin;

    public PlaceholderRequest(Plugin plugin) {
        super(plugin, "paintball");

        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String id) {

        if (player == null)
            return "";

        StatType type = null;

        for (StatType stat : StatType.values()) {

            if (id.equalsIgnoreCase(stat.getSignName())) {
                type = stat;
                break;
            }

        }

        if (type == null)
            return "";

        UUIDStatsFile statsFile = Settings.getSettings().getStatsFolder().getPlayerFile(player.getUniqueId(), false);

        if (statsFile == null) {
            return "0";
        } else {
            return statsFile.getStats(type);
        }
    }
}
