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
import org.bukkit.scheduler.BukkitScheduler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class Paintball extends JavaPlugin implements PluginMessageListener {

    @Override
    public void onEnable() {
        new Settings(this);

        CommandManager commandManager = new CommandManager();
        commandManager.init();

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

        /*
        Adds a diamond axe, with the name Forward, a 2-lined description that describes what it does.
        A worth of $0, so it requires no money (This requires Vault if you want it to be worth something)
        A worth of 10 KillCoins, so requires and removes 10 KillCoins when bought
        An expiration time of 300 seconds (5 minutes)
        Requires permission "paintball.item.forward"
        Sets the amount to 1
        And is shown in the inventory, true

        You can set KillCoins, expiration time, or money to 0 to remove requiring it
        You can also set permission, or description safely to null in order to remove a description or required permission
         */
        new KillCoinItem(Material.DIAMOND_AXE, "Jumper", "Gives you the ability to jump\n5 blocks up!", 0, 0, 10, "paintball.item.sneaker", 3, true) {
            int currentAmount = this.getAmount();
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                Action action = event.getAction();
                currentAmount = player.getItemInHand().getAmount();

                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    // teleport the player 5 blocks up
                    player.teleport(player.getLocation().add(0, 5, 0));

                    // remove 1 item from their inventory
                    currentAmount--;
                    if (currentAmount == 0) {
                        player.getInventory().remove(player.getItemInHand());
                    } else {
                        player.getItemInHand().setAmount(currentAmount);
                    }
                }
            }
        };

        new KillCoinItem(Material.SNOW_BALL, "Unlimited Snowballs", "Gives an unlimited snowball", 0, 0, 60, "paintball.item.unlimited", 2, true) {
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                Action action = event.getAction();

                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    player.launchProjectile(Snowball.class);
                    event.setCancelled(true);
                }
            }
        };

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();

            Metrics.Graph playersInArenaGraph = metrics.createGraph("Players In Arena");

            playersInArenaGraph.addPlotter(new Metrics.Plotter() {
                @Override
                public int getValue() {
                    return 22;
                }
            });

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