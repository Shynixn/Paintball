package me.synapz.paintball;

import me.synapz.paintball.commands.CommandManager;

import me.synapz.paintball.events.ChatSystem;
import me.synapz.paintball.events.JoinSigns;
import me.synapz.paintball.events.Listeners;
import me.synapz.paintball.events.PaintballShoot;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Paintball extends JavaPlugin {

    @Override
    public void onEnable() {
        Settings.getSettings().init(this);
        ArenaManager.getArenaManager().setup();
        CommandManager commandManager = new CommandManager();
        commandManager.init();

        Bukkit.getServer().getPluginManager().registerEvents(new PaintballShoot(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinSigns(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatSystem(), this);

        getCommand("paintball").setExecutor(commandManager);
    }

    @Override
    public void onDisable() {
        ArenaManager.getArenaManager().stopArenas();
    }
    
}