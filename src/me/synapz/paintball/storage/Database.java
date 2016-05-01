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
    private HashMap<UUID, Arena> bungeePlayers = new HashMap<>();

    public Database(Plugin pb) {
        super(pb, "database.yml");

        loadDatabaseValues();
        setupDatabase(pb);
    }

    private void setupDatabase(Plugin pb) {
        this.pb = pb;

        if (Databases.BUNGEE_ENABLED.getBoolean() && !Bukkit.getServer().getMessenger().getIncomingChannels().contains("BungeeCord")) {
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(pb, "BungeeCord", this);
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(pb, "BungeeCord");
        }

        if (Databases.SQL_ENABLED.getBoolean()) {
            setupSQL(pb, Databases.DATABASE.getString());
        }

        if (Databases.SERVER_ID.getString().equalsIgnoreCase("Generating")) {
            Random r = new Random(5);
            Integer base10ServerID = r.nextInt(999);
            String serverIDString = Integer.toString(base10ServerID);
            String serverID = Base64.getEncoder().encodeToString(serverIDString.getBytes());
            setValue("Bungee.serverID", serverID);
            loadDatabaseValues();
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
        saveFile();
    }

    //sql

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        if (Databases.MY_SQL.getBoolean()) {
            String port = Databases.HOST.getString().substring(Databases.HOST.getString().indexOf(":") + 1);
            MySQL MySQL = new MySQL(Databases.HOST.getString().replace(":" + port, ""), port, Databases.DATABASE.getString(), Databases.USERNAME.getString(), Databases.PASSWORD.getString());
            return MySQL.openConnection();
        } else {
            return DriverManager.getConnection(Databases.HOST.getString(), Databases.USERNAME.getString(), Databases.PASSWORD.getString());
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
        if (!channel.equals("BungeeCord") || !Databases.BUNGEE_ENABLED.getBoolean()) {
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
        if (serverID.equalsIgnoreCase(Databases.SERVER_ID.getString())) {
            String player = in.readUTF();
            String arenaName = in.readUTF();
            Arena a = ArenaManager.getArenaManager().getArena(arenaName);
            if (a.getMax() < a.getAllPlayers().size()) {
                ByteArrayDataOutput out1 = ByteStreams.newDataOutput();
                out1.writeUTF("Paintball");
                out1.writeUTF("Response");
                out1.writeUTF(player);
                out1.writeUTF("true");
                Bukkit.getServer().sendPluginMessage(pb, "BungeeCord", out1.toByteArray());
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(Databases.BUNGEE_ID.getString());
                Bukkit.getServer().sendPluginMessage(pb, "BungeeCord", out.toByteArray());
                UUID uuid = UUID.fromString(player);
                bungeePlayers.put(uuid, a);
            } else {
                ByteArrayDataOutput out1 = ByteStreams.newDataOutput();
                out1.writeUTF("Paintball");
                out1.writeUTF("Response");
                out1.writeUTF(player);
                out1.writeUTF("false");
                Bukkit.getServer().sendPluginMessage(pb, "BungeeCord", out1.toByteArray());
            }
            updateBungeeSigns();
        }
    }

    public void updateBungeeSigns() {
        if (!pb.isEnabled() || !Databases.BUNGEE_ENABLED.getBoolean()) return;
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
        out.writeUTF(Databases.SERVER_ID.getString());
        out.writeUTF(arenas);
        out.writeUTF(sign);
        Bukkit.getServer().sendPluginMessage(pb, "BungeeCord", out.toByteArray());
    }

    public HashMap<UUID, Arena> getBungeePlayers() {
        return bungeePlayers;
    }

    // Loads all enums to their value
    private void loadDatabaseValues() {
        for (Databases database : Databases.values()) {
            Databases.ReturnType returnType = database.getReturnType();

            switch (returnType) {
                case BOOLEAN:
                    database.setBoolean(loadBoolean(database));
                    break;
                case INT:
                    database.setInteger(loadInt(database));
                    break;
                case STRING:
                    database.setString(loadString(database));
                    break;
                default:
                    break;
            }
        }
    }
}
