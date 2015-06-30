package me.synapz.paintball;

import me.synapz.paintball.commands.CommandManager;

import me.synapz.paintball.events.PaintballShoot;
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

        getCommand("paintball").setExecutor(commandManager);
    }

    @Override
    public void onDisable() {
        // todo: add ArenaManager.getArenaManager().stopArenas() here
    }
    
}
