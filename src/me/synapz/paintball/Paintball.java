package me.synapz.paintball;

import me.synapz.paintball.commands.CommandManager;
import me.synapz.paintball.events.*;
import me.synapz.paintball.storage.PlayerData;
import me.synapz.paintball.storage.SQLStatisticsStorage;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Paintball extends JavaPlugin {

    PlayerData data;
    SQLStatisticsStorage sql;

    @Override
    public void onEnable() {
        Team.loadTeamColors();

        data = new PlayerData(this);
        Settings.getSettings();

        loadSQL();

        ArenaManager.getArenaManager().setup();
        CommandManager commandManager = new CommandManager();
        commandManager.init();

        Bukkit.getServer().getPluginManager().registerEvents(new PaintballShoot(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinSigns(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatSystem(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LeaderboardSigns(), this);

        getCommand("paintball").setExecutor(commandManager);
    }

    public void loadSQL() {
        if (getConfig().getBoolean("SQL")) {
            sql = new SQLStatisticsStorage(this);
            String host = getConfig().getString("SQL-Settings.host");
            Integer port = getConfig().getInt("SQL-Settings.port");
            String user = getConfig().getString("SQL-Settings.user");
            String pass = getConfig().getString("SQL-Settings.pass");
            String database = getConfig().getString("SQL-Settings.database");
            sql.setupSQL(host, port, user, pass, database);
        }
    }

    @Override
    public void onDisable() {
        ArenaManager.getArenaManager().stopArenas();
    }

    public PlayerData getPlayerData() {
        return data;
    }
}