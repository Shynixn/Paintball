package me.synapz.paintball.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Base64;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class SQLStatisticsStorage {

    // TODO: setting up SQL and methods to deal with storing encoded strings

    public FileConfiguration removeStats(FileConfiguration yaml) {
        Set<String> keys = yaml.getConfigurationSection("Player-Data").getKeys(false);
        HashMap<UUID, String> encodedStats = new HashMap<>();
        for (String key : keys) {
            ConfigurationSection stats = yaml.getConfigurationSection(key + ".Stats");
            String path = stats.getCurrentPath();
            String toEncode = path + ":" + stats;
            byte[] byteArray = toEncode.getBytes();
            String encoded = Base64.getEncoder().encode(byteArray).toString();
            UUID uuid = UUID.fromString(key.replace("Player-Data.", ""));
            encodedStats.put(uuid, encoded);
            yaml.set(path, null);
        }
        //TODO: Send HashMap of strings to SQL
        return yaml;
    }

    public FileConfiguration addStats(FileConfiguration yaml) {
        //TODO: get HashMap from SQL
        HashMap<UUID, String> encodedStats = new HashMap<>();
        for (UUID uuid : encodedStats.keySet()) {
            String encoded = encodedStats.get(uuid);
            String decoded = Base64.getDecoder().decode(encoded.getBytes()).toString();
            String[] split = decoded.split(":");
            yaml.set(split[0], split[1]);
        }
        return yaml;
    }
}
