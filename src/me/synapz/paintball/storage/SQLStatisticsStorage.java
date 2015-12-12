package me.synapz.paintball.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Base64;
import java.util.Set;

public class SQLStatisticsStorage {

    // TODO: setting up SQL and methods to deal with storing encoded strings

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
        //TODO: Send String/Yaml to SQL
        return yaml;
    }

    public FileConfiguration addStats(FileConfiguration yaml) {
        try {
            //TODO: get String/Yaml from SQL
            String base64Stats = null;
            String yamlString = Base64.getDecoder().decode(base64Stats.getBytes()).toString();
            YamlConfiguration statsYaml = new YamlConfiguration();
            statsYaml.loadFromString(yamlString);
            Set<String> keys = statsYaml.getConfigurationSection("Player-Data").getKeys(false);
            for (String key : keys) {
                ConfigurationSection stats = statsYaml.getConfigurationSection(key + ".Stats");
                String path = stats.getCurrentPath();
                yaml.set(path, stats);
            }
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return yaml;
    }
}
