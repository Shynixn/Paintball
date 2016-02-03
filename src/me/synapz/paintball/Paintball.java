package me.synapz.paintball;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.synapz.paintball.commands.CommandManager;
import me.synapz.paintball.events.*;
import me.synapz.paintball.killcoin.KillCoinItem;
import me.synapz.paintball.killcoin.KillCoinItemHandler;
import me.synapz.paintball.killcoin.KillCoinListeners;
import me.synapz.paintball.metrics.Metrics;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.ArenaFile;
import me.synapz.paintball.storage.PlayerData;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.Vector;

public class Paintball extends JavaPlugin implements PluginMessageListener {

    @Override
    public void onEnable() {
        new Settings(this);

        CommandManager commandManager = new CommandManager();
        commandManager.init();

        // TODD: on timer end check for winners
        // TODO: end time dont work

        Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinSigns(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatSystem(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LeaderboardSigns(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new KillCoinListeners(), this);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "PaintBall", this);
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "PaintBall");

        getCommand("paintball").setExecutor(commandManager);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                ArenaManager.getArenaManager().updateAllSignsOnServer();
                Paintball.this.bungeeUpdateSigns();
            }
        }, 0L, (long) Settings.SIGN_UPDATE_TIME);

        new KillCoinItem(Material.DIAMOND_BARDING, "Unlimited Paintballs", "Gives an unlimited paintball", 0, 0, 60, "", 1, true) {
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                Action action = event.getAction();

                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    player.launchProjectile(Snowball.class);
                }
            }
        };

        new KillCoinItem(Material.SUGAR, "Sugar Overdose", "Gives you a x2 speed boost for one minute", 0, 0, 0, "", 1, true) {
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                Action action = event.getAction();
                ItemStack itemInHand = player.getItemInHand();

                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (60*20), 2));
                    player.getInventory().remove(itemInHand);
                }
            }
        };

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

    //TODO: Find a good home for this
    public void bungeeUpdateSigns() {
        if (Settings.BUNGEE_CORD) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ArenaUpdate");
            out.writeUTF(Settings.SERVER_ID);
            //TODO: get arena states and names and make a string split with :'s
            // name:name:name
            // state:state:state
            // they must be in the correct order
            Bukkit.getServer().sendPluginMessage(this, "PaintBall", out.toByteArray());
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("PaintBall")) return;
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String command = in.readUTF();
            if (command.equals("IncomingPlayer")) {
                String uuid = in.readUTF();
                String aname = in.readUTF();
                String serverUUID = in.readUTF();
                UUID puuid = UUID.fromString(uuid);
                Player p = Bukkit.getPlayer(puuid);
                if (serverUUID.equalsIgnoreCase(getConfig().getString("ServerID"))) {
                    if (p != null) {
                        //TODO: add player to lobby of arena with name aname
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}