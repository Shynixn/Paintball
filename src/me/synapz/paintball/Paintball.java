package me.synapz.paintball;

import me.synapz.paintball.coin.CoinItemListener;
import me.synapz.paintball.coin.CoinItems;
import me.synapz.paintball.commands.CommandManager;
import me.synapz.paintball.events.ChatSystem;
import me.synapz.paintball.events.JoinSigns;
import me.synapz.paintball.events.LeaderboardSigns;
import me.synapz.paintball.events.Listeners;
import me.synapz.paintball.metrics.Metrics;
import me.synapz.paintball.storage.Settings;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
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
        - Better ActionBar (It flickers and don't work sometimes)
        - Make it so if the config values are not found it sets it in. - (Still does not work for things not in default
        - Fix command order
        - Fix leaderboard command
        - Global ranks in /pb stats also
        - Team Pick bug where teams might not be an even number
        - Leave signs
        - Spectator join signs
        - Better kill messages
        - Switch team wool numbers is wrong
        - Vault, Title, and ActionBar remove in config
        - Tie support
        - Reset stats command
        - Their nametag is not updating...
        - Sometimes hearts do not show
        - Particles on kill
        - Leaderboard sign for times played, counted from when a player leaves how much time the arena has been at
        - Grayish messages on prefix
        - No fly after leaving arena when starting with fly

        Future Features
        - Ranks
        - More Coin items
        - Command hovers
            - Arena hovers, (who is in the arena, setup, etc)
        - Spectate Autojoin and signs
        - Holographic displays that pop out, like +1$ and +1 score etc.
        - When a player dies tp them to the ground like they are dying
        - Autocorrect function
        */

        Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinSigns(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatSystem(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LeaderboardSigns(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CoinItemListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        getCommand("paintball").setExecutor(commandManager);

        CoinItems.getCoinItems().loadItems();

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            ArenaManager.getArenaManager().updateAllSignsOnServer();
        }, 0L, (long) Settings.SIGN_UPDATE_TIME);


        try {
            Metrics metrics = new Metrics(this);
            metrics.start();

            /*
            Metrics.Graph playersInArenaGraph = metrics.createGraph("Players In Arena");

            playersInArenaGraph.addPlotter(new Metrics.Plotter() {
                @Override
                public int getValue() {
                    return 22;
                }
            });
            */
        } catch (IOException exc) {

        }
    }

    @Override
    public void onDisable() {
        ArenaManager.getArenaManager().stopArenas();
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Settings.VAULT = false;
        } else {
            Settings.ECONOMY = rsp.getProvider();
        }
    }
}