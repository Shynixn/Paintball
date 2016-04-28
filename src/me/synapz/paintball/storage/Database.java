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

    private Plugin pb;

    public Database(Plugin pb) {
        super(pb, "database.yml");
    }

    public void setupDatabase(Plugin pb) {
        this.pb = pb;

        if (settings.bungee && !Bukkit.getServer().getMessenger().getIncomingChannels().contains("BungeeCord")) {
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(pb, "BungeeCord", this);
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(pb, "BungeeCord");
        }

        if (settings.sql) {
            setupSQL(pb, settings.database);
        }

        if (settings.bungee && settings.loadString(Databases.SERVER_ID).equalsIgnoreCase("Generate")) {
            Random r = new Random(5);
            Integer base10ServerID = r.nextInt(9999);
            String serverIDString = Integer.toString(base10ServerID);
            String serverID = Base64.getEncoder().encodeToString(serverIDString.getBytes());
            setValue("Bungee.serverID", serverID);
        }
    }

    /*
    If any of the following are null (not set) this will set the file with the default value
    and return the default value.
    */

    private void setValue(String path, Object object) {
        fileConfig.set(path, object);
    }

    //sql

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        if (settings.mysql) {
            String port = settings.host.substring(settings.host.indexOf(":") + 1);
            MySQL MySQL = new MySQL(settings.host.replace(":" + port, ""), port, settings.database, settings.username, settings.password);
            return MySQL.openConnection();
        } else {
            return DriverManager.getConnection(settings.host, settings.username, settings.password);
        }
    }

    public void setupSQL(Plugin pb, String database) {
        try {
            Connection conn;
            conn = getConnection();
            PreparedStatement createDatabase = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS " + database);
            createDatabase.execute();
            PreparedStatement createTable = conn.prepareStatement("CREATE TABLE IF NOT EXISTS Paintball_Stats" +
                    " (id INT NOT NULL,stats LONGTEXT,PRIMARY KEY (id));");
            createTable.execute();
            PreparedStatement query = conn.prepareStatement("SELECT stats FROM Paintball_Stats WHERE id = 1;");
            ResultSet result = query.executeQuery();
            if (!result.next()) {
                Bukkit.getLogger().info("sql ready!");
                return;
            }
            String encoded = result.getString("stats");
            File file = new File(pb.getDataFolder(), "playerdata.yml");
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            yaml.set("Stats", encoded);
            yaml.save(file);
            Bukkit.getLogger().info("Downloaded stats from sql!");
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Bukkit.getLogger().info("Failed to download sql backup!");
        }
    }

    public FileConfiguration addStats(FileConfiguration yaml) {
        YamlConfiguration statsYaml = new YamlConfiguration();
        try {
            Connection conn;
            conn = getConnection();
            PreparedStatement sql = conn.prepareStatement("SELECT stats FROM Paintball_Stats WHERE id = 1;");
            ResultSet result = sql.executeQuery();
            result.next();
            String base64Stats = result.getString("stats");
            String yamlString = Base64.getDecoder().decode(base64Stats.getBytes()).toString();
            statsYaml.loadFromString(yamlString);
        } catch (InvalidConfigurationException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            Bukkit.getLogger().info("sql connection failed! Using offline backup until we can connect again");
            if (yaml.contains("Stats")) {
                String base64Stats = yaml.getString("Stats");
                String yamlString = Base64.getDecoder().decode(base64Stats.getBytes()).toString();
                try {
                    statsYaml.loadFromString(yamlString);
                } catch (InvalidConfigurationException e1) {
                    e1.printStackTrace();
                    Bukkit.getLogger().severe("Failed to load offline config! Please check sql connection and playerdata file!");
                }
            } else {
                Bukkit.getLogger().severe("Statistics Down!! We have no sql connection and don't have a backup of stats!");
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

    public FileConfiguration removeStats(FileConfiguration yaml) {
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
            PreparedStatement sql = conn.prepareStatement("INSERT INTO Paintball_Stats (id,stats) VALUES (1,'" + encoded + "') ON DUPLICATE KEY UPDATE id = " + 1 + ",stats = '" + encoded + "';");
            sql.execute();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            Bukkit.getLogger().info("Failed to upload sql!");
        }
        return yaml;
    }

    //Bungee

    public void onPluginMessageReceived(String channel, Player sender, byte[] message) {
        if (!channel.equals("BungeeCord") || !settings.bungee) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (!subchannel.equals("Paintball")) {
            return;
        }
        String cmd = in.readUTF();
        if (!cmd.equalsIgnoreCase("IncomingPlayer")) {
            return;
        }
        String serverID = in.readUTF();
        if (serverID.equalsIgnoreCase(settings.SID)) {
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
                out.writeUTF(settings.BID);
                Bukkit.getServer().sendPluginMessage(pb, "BungeeCord", out.toByteArray());
                UUID uuid = UUID.fromString(player);
                settings.bungeePlayers.put(uuid, a);
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

    public void updateBungeeSigns() {
        if (!pb.isEnabled() || !settings.bungee) return;
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
        out.writeUTF(settings.SID);
        out.writeUTF(arenas);
        out.writeUTF(sign);
        Bukkit.getServer().sendPluginMessage(pb, "BungeeCord", out.toByteArray());
    }

    public HashMap<UUID, Arena> getBungeePlayers() {

        return settings.bungeePlayers;
    }
}
