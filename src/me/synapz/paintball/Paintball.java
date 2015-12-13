package me.synapz.paintball;

import me.synapz.paintball.commands.CommandManager;
import me.synapz.paintball.events.*;
import me.synapz.paintball.storage.PlayerData;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Paintball extends JavaPlugin {

    PlayerData data;

    @Override
    public void onEnable() {
        Team.loadTeamColors();

        this.data = new PlayerData(this);
        Settings.getSettings();

        this.loadSQL();

        ArenaManager.getArenaManager().setup();
        CommandManager commandManager = new CommandManager();
        commandManager.init();

        Bukkit.getServer().getPluginManager().registerEvents(new PaintballShoot(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinSigns(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatSystem(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LeaderboardSigns(), this);

        this.getCommand("paintball").setExecutor(commandManager);
    }

    public void loadSQL() {
        if (this.getConfig().getBoolean("SQL")) {
            String host = this.getConfig().getString("SQL-Settings.host");
            Integer port = this.getConfig().getInt("SQL-Settings.port");
            String user = this.getConfig().getString("SQL-Settings.user");
            String pass = this.getConfig().getString("SQL-Settings.pass");
            String database = this.getConfig().getString("SQL-Settings.database");
            this.data.setupSQL(host, port, user, pass, database);
        }
    }

    @Override
    public void onDisable() {
        ArenaManager.getArenaManager().stopArenas();
    }

    // TODO: bad way of getting player data, find better way
    public PlayerData getPlayerData() {
        return this.data;
    }
}