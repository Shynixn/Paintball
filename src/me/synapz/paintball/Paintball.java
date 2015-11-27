package me.synapz.paintball;

import me.synapz.paintball.commands.CommandManager;

import me.synapz.paintball.events.*;
import me.synapz.paintball.storage.PlayerData;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.storage.Statistics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class Paintball extends JavaPlugin {

    @Override
    public void onEnable() {
        Team.loadTeamColors();

        new PlayerData(this);
        new Statistics(this);
        Settings.getSettings();

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

    @Override
    public void onDisable() {
        ArenaManager.getArenaManager().stopArenas();
    }
    
}