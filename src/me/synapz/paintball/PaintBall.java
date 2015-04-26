package me.synapz.paintball;

import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Paintball extends JavaPlugin{ 

    @Override
    public void onEnable() {
        Settings.getSettings().init(this);
        ArenaManager.getArenaManager().setup();
        CommandManager commandManager = new CommandManager();
        commandManager.init();

        getCommand("paintball").setExecutor(commandManager);
    }

    @Override
    public void onDisable() {
    }
}
