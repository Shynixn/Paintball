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
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
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
import java.util.HashSet;
import java.util.UUID;
import java.util.Vector;

public class Paintball extends JavaPlugin implements PluginMessageListener {

    @Override
    public void onEnable() {
        new Settings(this);

        CommandManager commandManager = new CommandManager();
        commandManager.init();

        // fixed join signs so it joins arena with most players
            // Add this to /pb join with arena and team as optional
            // Do this same this when picking for teams?
        // TODD: on timer end check for winners
        // TODO: end time dont work, it should show on scoreboards, fixed this?
        // spectate setting/removing al functional?
            // no.. make sure they cant remove if it is under 0

        Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinSigns(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatSystem(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LeaderboardSigns(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new KillCoinListeners(), this);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "PaintBall", this);
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "PaintBall");

        getCommand("paintball").setExecutor(commandManager);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            ArenaManager.getArenaManager().updateAllSignsOnServer();
            Paintball.this.bungeeUpdateSigns();
        }, 0L, (long) Settings.SIGN_UPDATE_TIME);

        new KillCoinItem(Material.SNOW_BALL, ChatColor.RESET + Settings.THEME + "Paintball", 64, true, "Default Paintball Item", 0.0, 0, 0, "", Sound.CLICK);
        // 2
        new KillCoinItem(Material.SUGAR, "Sugar Overdose", 1, true, "Speeds up your movement by 2x", 0.0, 0, 0, "", Sound.BURP) {
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                ItemStack itemInHand = player.getItemInHand();
                Action action = event.getAction();


                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (60*20), 2));
                    player.getInventory().remove(itemInHand);
                }
            }
        };

        // 3
        new KillCoinItem(Material.IRON_AXE, "Unlimited Paintballs", 1, true, "Receive an unlimited amount of Paintballs!", 0.0, 0, 120, "", Sound.CLICK) {
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                Action action = event.getAction();

                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    player.launchProjectile(Snowball.class);
                }
            }
        };

        // 7
        new KillCoinItem(Material.IRON_BARDING, "Machine Gun", 1, true, "Shoot many Paintballs at a time", 0.0, 0, 120, "", Sound.WOOD_CLICK) {
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                Action action = event.getAction();

                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    for (int i = 0; i < 10; i++) {
                        player.launchProjectile(Snowball.class);
                    }
                }
            }
        };

        // 10
        new KillCoinItem(Material.DIAMOND_BARDING, "Rocket Launcher", 1, true, "Shoot a giant wave of Paintballs", 0.0, 0, 0, "", Sound.ENDERDRAGON_DEATH) {
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                Action action = event.getAction();

                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    for (int i = 0; i < 50; i++) {
                        player.launchProjectile(Snowball.class);
                    }
                    player.getInventory().remove(player.getItemInHand());
                }
            }
        };

        new KillCoinItem(Material.ANVIL, "Tank", 1, true, "Become a Tank! You will be \ninvisible, have unlimited Paintballs, and move\nslow like a Tank.", 0.0, 0, 0, "", Sound.AMBIENCE_THUNDER) {
            @Override
            public void onClickItem(PlayerInteractEvent event){
                Player player = event.getPlayer();

                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2, 60));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 60));
                player.setGameMode(GameMode.CREATIVE);
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