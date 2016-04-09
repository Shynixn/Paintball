package me.synapz.paintball.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Database extends PaintballFile implements PluginMessageListener {

    public static Boolean SQL = false;
    public static HashMap<UUID, Arena> bungeePlayers = new HashMap<>();
    private static String host = null;
    private static String username = null;
    private static String password = null;
    private static Plugin pb = null;
    private static String SID = null;
    public Boolean bungee = false;
    private String BID = null;
    private String database = null;

    public Database(Plugin pb) {
        super(pb, "database.yml");

        this.pb = pb;

        if (loadBoolean("SQL.enabled")) {
            SQL = true;
            host = loadString("SQL.host");
            username = loadString("SQL.username");
            password = loadString("SQL.password");
            database = loadString("SQL.database");
        }
        if (loadBoolean("Bungee.enabled")) {
            bungee = true;
            BID = loadString("Bungee.bungeeID");
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(pb, "BungeeCord");
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(pb, "BungeeCord", this);
            if (loadString("Bungee.serverID").equalsIgnoreCase("Generate")) {
                Random r = new Random(5);
                String base10ServerID = r.doubles(1073741824).toString();
                String serverID = Base64.getEncoder().encodeToString(base10ServerID.getBytes());
                setValue("Bungee.serverID", serverID);
                SID = serverID;
            }
        }
    }

    private Object loadValue(String path) {
        Object value = fileConfig.get(path);

        // If this value is null, it was not found, so turn this file to database_backup.yml and load another updated one
        if (value == null) {
            Settings.getSettings().backupConfig("database");
            return null;
        }

        // After backup and new one is done, get the value
        return value;
    }

    private void setValue(String path, Object object) {
        fileConfig.set(path, object);
    }

    public Boolean isBungee() {
        return bungee;
    }

    public Boolean isSQL() {
        return SQL;
    }

    private int loadInt(String path) {
        return (int) loadValue(path);
    }

    private String loadString(String path) {
        return (String) loadValue(path);
    }

    private boolean loadBoolean(String path) {
        return (boolean) loadValue(path);
    }

    //SQL

    public void setupSQL(Plugin pb, String host, String username, String password, String database) {
        SQL = true;
        Database.host = host;
        Database.username = username;
        Database.password = password;
        this.database = database;
        this.pb = pb;
        try {
            Connection conn;
            conn = DriverManager.getConnection(host, username, password);
            PreparedStatement sql = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS " + database);
            sql.execute();
            PreparedStatement sql0 = conn.prepareStatement("CREATE TABLE IF NOT EXISTS Paintball_Stats" +
                    " (id INTEGER not null,stats STRING,PRIMARY KEY (id))");
            sql0.execute();
            PreparedStatement sql1 = conn.prepareStatement("SELECT stats FROM `Paintball_Stats` WHERE id = '1';");
            ResultSet result = sql1.executeQuery();
            result.next();
            String encoded = result.getString("stats");
            File file = new File(pb.getDataFolder(), "playerdata.yml");
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            yaml.set("Stats", encoded);
            yaml.save(file);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            Bukkit.getLogger().info("Failed to download SQL backup!");
        }
    }

    public static FileConfiguration addStats(FileConfiguration yaml) {
        YamlConfiguration statsYaml = new YamlConfiguration();
        try {
            Connection conn;
            conn = DriverManager.getConnection(host, username, password);
            PreparedStatement sql = conn.prepareStatement("SELECT stats FROM `Paintball_Stats` WHERE id = '1';");
            ResultSet result = sql.executeQuery();
            result.next();
            String base64Stats = result.getString("stats");
            String yamlString = Base64.getDecoder().decode(base64Stats.getBytes()).toString();
            statsYaml.loadFromString(yamlString);
        } catch (InvalidConfigurationException | SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().info("SQL connection failed! Using offline backup until we can connect again");
            if (yaml.contains("Stats")) {
                String base64Stats = yaml.getString("Stats");
                String yamlString = Base64.getDecoder().decode(base64Stats.getBytes()).toString();
                try {
                    statsYaml.loadFromString(yamlString);
                } catch (InvalidConfigurationException e1) {
                    e1.printStackTrace();
                    Bukkit.getLogger().severe("Failed to load offline config! Please check SQL connection and playerdata file!");
                }
            } else {
                Bukkit.getLogger().severe("Statistics Down!! We have no SQL connection and don't have a backup of stats!");
            }
        }

        Set<String> keys = statsYaml.getConfigurationSection("Player-Data").getKeys(false);
        for (String key : keys) {
            ConfigurationSection stats = statsYaml.getConfigurationSection(key + ".Stats");
            String path = stats.getCurrentPath();
            yaml.set(path, stats);
        }
        return yaml;
    }

    public static FileConfiguration removeStats(FileConfiguration yaml) {
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
        try {
            Connection conn;
            conn = DriverManager.getConnection(host, username, password);
            PreparedStatement sql = conn.prepareStatement("INSERT INTO Paintball_Stats (id,stats) VALUES (1," + encoded + ")");
            sql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().info("Failed to upload SQL!");
        }
        return yaml;
    }

    //Bungee

    public void onPluginMessageReceived(String channel, Player sender, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("Paintball")) {
            String cmd = in.readUTF();
            if (cmd.equalsIgnoreCase("IncomingPlayer")) {
                String serverID = in.readUTF();
                if (serverID.equalsIgnoreCase(this.SID)) {
                    String player = in.readUTF();
                    String arenaName = in.readUTF();
                    Arena a = ArenaManager.getArenaManager().getArena(arenaName);
                    if (a.getMax() < a.getAllPlayers().size()) {
                        ByteArrayDataOutput out1 = ByteStreams.newDataOutput();
                        out1.writeUTF("Paintball");
                        out1.writeUTF("Responce");
                        out1.writeUTF(player);
                        out1.writeUTF("true");
                        Bukkit.getServer().sendPluginMessage(pb, "BungeeCord", out1.toByteArray());
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("Connect");
                        out.writeUTF(BID);
                        Bukkit.getServer().sendPluginMessage(pb, "BungeeCord", out.toByteArray());
                        UUID uuid = UUID.fromString(player);
                        bungeePlayers.put(uuid, a);
                    } else {
                        ByteArrayDataOutput out1 = ByteStreams.newDataOutput();
                        out1.writeUTF("Paintball");
                        out1.writeUTF("Responce");
                        out1.writeUTF(player);
                        out1.writeUTF("false");
                        Bukkit.getServer().sendPluginMessage(pb, "BungeeCord", out1.toByteArray());
                    }
                    updateBungeeSigns();
                }
            }
        }
    }

    // TODO: This is returning null and breaks this whole thing onEnable and on leave *Fixed*
    public static void updateBungeeSigns() {
        int numb = 0;
        String arenas = "";
        String sign = "";
        for (String an : ArenaManager.getArenaManager().getArenas().keySet()) {
            Arena a = ArenaManager.getArenaManager().getArenas().get(an);
            if (numb != 0) {
                arenas = arenas + ":" + a.getName();
                sign = sign + ":" + a.getStateAsString();
            } else {
                arenas = arenas + a.getName();
                sign = sign + a.getStateAsString();
            }
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Paintball");
        out.writeUTF("Arenas");
        out.writeUTF(SID);
        out.writeUTF(arenas);
        out.writeUTF(sign);
        Bukkit.getServer().sendPluginMessage(pb, "BungeeCord", out.toByteArray());
    }

}
