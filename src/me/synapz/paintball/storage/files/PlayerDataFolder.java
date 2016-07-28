package me.synapz.paintball.storage.files;

import me.synapz.paintball.storage.Settings;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataFolder extends PaintballFile {

    private Map<UUID, UUIDPlayerDataFile> files = new HashMap<>();

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

            if (Settings.getSettings().getPlayerDataFolder().getPlayerFile(uuid) != null) {
                // is already in list, continue
                continue;
            }

            new UUIDPlayerDataFile(uuid);
        }
    }

    public UUIDPlayerDataFile getPlayerFile(UUID uuid) {
        return files.get(uuid);
    }

    public Collection<UUIDPlayerDataFile> getPlayerDataList() {
        return files.values();
    }

    public void addPlayerFile(UUIDPlayerDataFile file) {
        files.put(file.getUUID(), file);
    }

    public void removePlayerFile(UUID uuid) {
        files.remove(uuid).delete();
    }
}