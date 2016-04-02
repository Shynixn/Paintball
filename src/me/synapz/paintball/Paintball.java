package me.synapz.paintball;

import me.synapz.paintball.coin.CoinItemListener;
import me.synapz.paintball.commands.CommandManager;
import me.synapz.paintball.events.ChatSystem;
import me.synapz.paintball.events.JoinSigns;
import me.synapz.paintball.events.LeaderboardSigns;
import me.synapz.paintball.events.Listeners;
import me.synapz.paintball.metrics.Metrics;
import me.synapz.paintball.players.CTFArenaPlayer;
import me.synapz.paintball.storage.Settings;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Paintball extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        new Settings(this);
        this.setupEconomy();

        CommandManager commandManager = new CommandManager();
        commandManager.init();

        /*
        Non-Important Things
        - Make it so if the config values are not found it sets it in. - (Still does not work for things not in default
        - Team Pick bug where teams might not be an even number
        - Leave signs
        - Spectator join signs
        - Tie support
        - Leaderboard sign for times played, counted from when a player leaves how much time the arena has been at
        - Spectator thing add next page
        - Way better ActionBar
        - Fix double messages

        Future Features
        - Ranks
        - More Coin items
        - Command hovers
            - Arena hovers, (who is in the arena, setup, etc)
        - Spectate Autojoin and signs
        - Holographic displays that pop out, like +1$ and +1 score etc.
        */

        PluginManager pm = Bukkit.getServer().getPluginManager();

        pm.registerEvents(new Listeners(), this);
        pm.registerEvents(new JoinSigns(), this);
        pm.registerEvents(new ChatSystem(), this);
        pm.registerEvents(new LeaderboardSigns(), this);
        pm.registerEvents(new CoinItemListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        getCommand("paintball").setExecutor(commandManager);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            ArenaManager.getArenaManager().updateAllSignsOnServer();
        }, 0L, (long) Settings.SIGN_UPDATE_TIME);

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException exc) {

        }
    }

    @Override
    public void onDisable() {
        ArenaManager.getArenaManager().stopArenas();
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
}