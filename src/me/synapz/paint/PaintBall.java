package me.synapz.paint;

import me.synapz.paint.arenas.ArenaManager;
import me.synapz.paint.commands.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PaintBall extends JavaPlugin{

    // TODO: sign to leave red arena and go to blue arena?
    @Override
    public void onEnable() {
        ArenaManager.getArenaManager().setup();

        CommandManager commandManager = new CommandManager();
        commandManager.init();

        getCommand("paintball").setExecutor(commandManager);
    }

    @Override
    public void onDisable() {

    }
}
