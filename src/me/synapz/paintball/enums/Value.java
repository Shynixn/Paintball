package me.synapz.paintball.enums;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.generator.InternalChunkGenerator;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public enum Value {

    /*version     = pluginYML.getVersion();
    website     = pluginYML.getWebsite();
    author      = pluginYML.getAuthors().toString();
    prefix      = translateAlternateColorCodes('&', configFile.getString("prefix"));
    theme       = translateAlternateColorCodes('&', configFile.getString("theme-color"));
    secondary   = translateAlternateColorCodes('&', configFile.getString("secondary-color"));

    // arena values
    SPLASH_PAINTBALLS       = configFile.getBoolean("paintball-splash");
    COLOR_PLAYER_TITLE      = configFile.getBoolean("color-player-title");
    WOOL_HELMET             = configFile.getBoolean("give-wool-helmet");
    LOBBY_COUNTDOWN         = configFile.getInt("countdown.lobby.countdown");
    LOBBY_INTERVAL          = configFile.getInt("countdown.lobby.interval");
    LOBBY_NO_INTERVAL       = configFile.getInt("countdown.lobby.no-interval");
    ARENA_COUNTDOWN         = configFile.getInt("countdown.arena.countdown");
    ARENA_INTERVAL          = configFile.getInt("countdown.arena.interval");
    ARENA_NO_INTERVAL       = configFile.getInt("countdown.arena.no-interval");
    TITLE_API               = configFile.getBoolean("title-api") && Bukkit.getPluginManager().getPlugin("TitleAPI") != null;
    */
    WEBSITE("spigot"),
    AUTHOR("Synapz_"),
    PREFIX("prefix", DARK_GRAY + "[" + AQUA + "Paintball" + DARK_GRAY + "] "),
    THEME_COLOR("theme-color", DARK_AQUA),
    SECONDARY_COLOR("secondary-color", GRAY),
    PAINTBALL_SPLASH("paintball-splash", true),
    COLOR_PLAYER_TITLE("color-player-title", true),
    WOOL_HELMET("give-wool-helmet", true),
    LOBBY_COUNTDOWN("countdown.lobby.countdown", 60),
    LOBBY_INTERVAL("countdown.lobby.interval", 10),
    LOBBY_NO_INTERVAL("countdown.lobby.no-interval", 5),
    ARENA_COUNTDOWN("countdown.arena.countdown", 10),
    ARENA_INTERVAL("countdown.arena.interval", 0),
    ARENA_NO_INTERVAL("countdown.arena.no-interval", 9),
    SPEC_CHAT("Chat.spectator-chat", GRAY + "[Spectator] %PLAYER%: %MSG%"),
    ARENA_CHAT("Chat.arena-chat", "%PREFIX%" + DARK_GRAY + " -%TEAMCOLOR%%TEAMNAME%" + DARK_GRAY + "- " + GREEN + "%PLAYER%" + GRAY + ": %MSG%"),
    TITLE_API("title-api", true);

    private String path;
    private Object defaultValue;

    Value(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;

        FileConfiguration file = Settings.getSettings().getConfigFile();
        if (defaultValue instanceof Boolean) {
            value = file.getBoolean(path);
        } else if (defaultValue instanceof String) {
            value = file.getString(path);
        } else if (defaultValue instanceof Integer) {
            value = file.getInt(path);
        }
        if (value == null) {
            value = defaultValue;
        }
    }

    Value(String value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
