package me.synapz.paintball;

import com.sun.media.jfxmedia.logging.Logger;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.bungee.BungeeManager;
import me.synapz.paintball.coin.CoinItemListener;
import me.synapz.paintball.commands.CommandManager;
import me.synapz.paintball.enums.Databases;
import me.synapz.paintball.enums.ScoreboardLine;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.listeners.*;
import me.synapz.paintball.metrics.Metrics;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.storage.files.PaintballFile;
import me.synapz.paintball.storage.files.UUIDFile;
import me.synapz.paintball.utils.Update;
import me.synapz.paintball.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

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

        removeOldPlayerDatsFile();
    }

    @Override
    public void onDisable() {
        ArenaManager.getArenaManager().stopArenas();

        if (Settings.HOLOGRAPHIC_DISPLAYS)
            Settings.ARENA.deleteLeaderboards();
    }

    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            Settings.VAULT = false;
            return;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Settings.VAULT = false;
        } else {
            Settings.ECONOMY = rsp.getProvider();
            Settings.VAULT = true;

            if (Settings.ECONOMY == null)
                Settings.VAULT = false;
        }
    }

    private void removeOldPlayerDatsFile() {
        List<File> files = Arrays.asList(this.getDataFolder().listFiles());
        File playerDataFile = null;

        for (File file : files) {
            if (file.getName().equals("playerdata.yml")) {
                playerDataFile = file;
                break;
            }
        }

        if (playerDataFile == null)
            return;

        this.getLogger().log(Level.INFO, "[Paintball] Converting old playerdata.yml files into folder...");

        FileConfiguration playerdata = new PaintballFile(this, "playerdata.yml").getFileConfig();

        if (playerdata.getConfigurationSection("Player-Data") == null) {
            playerDataFile.delete();
            this.getLogger().log(Level.INFO, "[Paintball] Conversion complete.");
            return;
        }

        for (String uuid : playerdata.getConfigurationSection("Player-Data").getKeys(false)) {
            UUIDFile uuidFile = new UUIDFile(UUID.fromString(uuid));

            for (StatType type : StatType.values()) {
                String path = type.getPath(UUID.fromString(uuid));

                uuidFile.getFileConfig().set(path, playerdata.getInt(path));
            }

            uuidFile.saveFile();
        }
        playerDataFile.delete();

        this.getLogger().log(Level.INFO, "[Paintball] Conversion complete.");
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