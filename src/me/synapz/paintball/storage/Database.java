package me.synapz.paintball.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.enums.Databases;
import me.synapz.paintball.storage.sql.mysql.MySQL;
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

    public static boolean SQL = false;
    public static boolean mySQL = true;
    public static HashMap<UUID, Arena> bungeePlayers = new HashMap<>();
    private static String host = null;
    private static String username = null;
    private static String password = null;
    private static String database = null;
    private static Plugin pb = null;
    private static String SID = "Generate";
    public boolean bungee = false;
    private String BID = null;

    public Database(Plugin pb) {
        super(pb, "database.yml");
    }

    public void setupDatabase(Plugin pb) {
        this.pb = pb;

        SQL = loadBoolean(Databases.SQL_ENABLED);
        mySQL = loadBoolean(Databases.MY_SQL);
        host = loadString(Databases.HOST);
        username = loadString(Databases.USERNAME);
        password = loadString(Databases.PASSWORD);
        database = loadString(Databases.DATABASE);

        bungee = loadBoolean(Databases.BUNGEE_ENABLED);

        if (!Bukkit.getServer().getMessenger().getIncomingChannels().contains("BungeeCord")) {
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(pb, "BungeeCord", this);
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(pb, "BungeeCord");
        }

        if (SQL) {
            setupSQL(pb, host, username, password, database);
        }

        if (loadString(Databases.SERVER_ID).equalsIgnoreCase("Generate")) {
            Random r = new Random(5);
            Integer base10ServerID = r.nextInt(9999);
            String serverIDString = Integer.toString(base10ServerID);
            String serverID = Base64.getEncoder().encodeToString(serverIDString.getBytes());
            setValue("Bungee.serverID", serverID);
        }
        if (SQL) {
            setupSQL(pb, host, username, password, database);
        }
    }

    /*
    If any of the following are null (not set) this will set the file with the default value
    and return the default value.
    */
    private int loadInt(Databases type) {
        if (isFoundInConfig(type))
            return (int) loadValue(type);
        else
            fileConfig.set(type.getPath(), type.getDefaultInt());

        saveFile();

        return type.getDefaultInt();
    }

    private String loadString(Databases type) {
        if (isFoundInConfig(type))
            return (String) loadValue(type);
        else
            fileConfig.set(type.getPath(), type.getDefaultString());

        saveFile();

        return type.getDefaultString();
    }

    private boolean loadBoolean(Databases type) {
        if (isFoundInConfig(type))
            return (boolean) loadValue(type);
        else
            fileConfig.set(type.getPath(), type.getDefaultBoolean());

        saveFile();

        return type.getDefaultBoolean();
    }

    private Object loadValue(Databases type) {
        return fileConfig.get(type.getPath());
    }

    private boolean isFoundInConfig(Databases type) {
        Object value = fileConfig.get(type.getPath());

        return value != null;
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

    //SQL

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (mySQL) {
            String port = host.substring(host.indexOf(":") + 1);
            MySQL MySQL = new MySQL(host.replace(":" + port, ""), port, database, username, password);
            return MySQL.openConnection();
        } else {
            return DriverManager.getConnection(host, username, password);
        }
    }

    public void setupSQL(Plugin pb, String host, String username, String password, String database) {
        try {
            Connection conn;
            conn = getConnection();
            PreparedStatement sql = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS " + database);
            sql.execute();
            PreparedStatement sql0 = conn.prepareStatement("CREATE TABLE IF NOT EXISTS Paintball_Stats" +
                    " (id INTEGER not null,stats STRING,PRIMARY KEY (id))");
            sql0.execute();
            PreparedStatement sql1 = conn.prepareStatement("SELECT stats FROM `Paintball_Stats` WHERE id = '1';");
            ResultSet result = sql1.executeQuery();
            result.next();
            String encoded = result.getString("stats");
            if (encoded == null) {
                Bukkit.getLogger().info("SQL ready!");
                return;
            }
            File file = new File(pb.getDataFolder(), "playerdata.yml");
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            yaml.set("Stats", encoded);
            yaml.save(file);
            Bukkit.getLogger().info("Downloaded stats from SQL!");
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Bukkit.getLogger().info("Failed to download SQL backup!");
        }
    }

    public static FileConfiguration addStats(FileConfiguration yaml) {
        YamlConfiguration statsYaml = new YamlConfiguration();
        try {
            Connection conn;
            conn = getConnection();
            PreparedStatement sql = conn.prepareStatement("SELECT stats FROM `Paintball_Stats` WHERE id = '1';");
            ResultSet result = sql.executeQuery();
            result.next();
            String base64Stats = result.getString("stats");
            String yamlString = Base64.getDecoder().decode(base64Stats.getBytes()).toString();
            statsYaml.loadFromString(yamlString);
        } catch (InvalidConfigurationException | SQLException | ClassNotFoundException e) {
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
            String path = "Player-Data." + key + ".Stats";
            ConfigurationSection stats = yaml.getConfigurationSection(path);

            yaml.set(path, stats);
        }
        return yaml;
    }

    public static FileConfiguration removeStats(FileConfiguration yaml) {
        Set<String> keys = yaml.getConfigurationSection("Player-Data").getKeys(false);
        YamlConfiguration statsYaml = new YamlConfiguration();
        for (String key : keys) {
            String path = "Player-Data." + key + ".Stats";
            ConfigurationSection stats = yaml.getConfigurationSection(path);

            statsYaml.set(path, stats);
            yaml.set(path, null);
        }
        byte[] byteArray = statsYaml.saveToString().getBytes();
        String encoded = Base64.getEncoder().encode(byteArray).toString();
        yaml.set("Stats", encoded);
        try {
            Connection conn;
            conn = getConnection();
            PreparedStatement sql = conn.prepareStatement("INSERT INTO Paintball_Stats (id,stats) VALUES (1," + encoded + ")");
            sql.execute();
        } catch (SQLException | ClassNotFoundException e) {
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

    public static void updateBungeeSigns() {
        if (pb.isEnabled()) {
            int numb = 0;
            String arenas = "";
            String sign = "";
            for (String an : ArenaManager.getArenaManager().getArenas().keySet()) {
                Arena a = ArenaManager.getArenaManager().getArenas().get(an);
                if (numb != 0) {
                    arenas = arenas + ":" + a.getName();
                    sign = sign + ":" + a.getSign();
                } else {
                    arenas = a.getName();
                    sign = a.getSign();
                }
                numb++;
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
}
