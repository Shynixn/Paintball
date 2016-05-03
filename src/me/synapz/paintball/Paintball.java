package me.synapz.paintball;

import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.bungee.BungeeManager;
import me.synapz.paintball.coin.CoinItemListener;
import me.synapz.paintball.commands.CommandManager;
import me.synapz.paintball.listeners.*;
import me.synapz.paintball.metrics.Metrics;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Update;
import me.synapz.paintball.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Paintball extends JavaPlugin implements Listener {

    public static boolean IS_1_9;
    private static Paintball instance;
    private BungeeManager bungeeManager;

    @Override
    public void onEnable() {
        instance = this;

        this.IS_1_9 = is1_9();

        new Settings(this);
        bungeeManager = new BungeeManager(this);
        this.setupEconomy();

        CommandManager commandManager = new CommandManager();
        commandManager.init();

        PluginManager pm = Bukkit.getServer().getPluginManager();

        pm.registerEvents(new Listeners(), this);
        pm.registerEvents(new JoinSigns(), this);
        pm.registerEvents(new ChatSystem(), this);
        pm.registerEvents(new LeaderboardSigns(), this);
        pm.registerEvents(new CoinItemListener(), this);
        pm.registerEvents(this, this);

        if (IS_1_9)
            pm.registerEvents(new Listeners1_9(), this);

        getCommand("paintball").setExecutor(commandManager);

        ArenaManager.getArenaManager().updateAllSignsOnServer();

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException exc) {

        }

        new Update(this);
    }

    @Override
    public void onDisable() {
        ArenaManager.getArenaManager().stopArenas();
        Settings.PLAYERDATA.saveFile();

        if (Settings.HOLOGRAPHIC_DISPLAYS)
            Settings.ARENA.deleteLeaderboards();
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Settings.VAULT = false;
        } else {
            Settings.ECONOMY = rsp.getProvider();

            if (Settings.ECONOMY == null)
                Settings.VAULT = false;
        }
    }

    public static Paintball getInstance() {
        return instance;
    }

    public BungeeManager getBungeeManager() {
        return bungeeManager;
    }

    private boolean is1_9() {
        try {
            Sound.BLOCK_COMPARATOR_CLICK.toString();
            return true;
        } catch (NoSuchFieldError exc) {
            return false;
        }
    }
}