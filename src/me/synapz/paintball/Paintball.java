package me.synapz.paintball;

import me.synapz.paintball.commands.CommandManager;
import me.synapz.paintball.events.*;
import me.synapz.paintball.storage.PlayerData;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.UUID;

public class Paintball extends JavaPlugin implements PluginMessageListener {

    PlayerData data;

    @Override
    public void onEnable() {
        Team.loadTeamColors();

        this.data = new PlayerData(this);
        Settings.getSettings();

        ArenaManager.getArenaManager().setup();
        CommandManager commandManager = new CommandManager();
        commandManager.init();

        Bukkit.getServer().getPluginManager().registerEvents(new PaintballShoot(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinSigns(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatSystem(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LeaderboardSigns(), this);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "PaintBall", this);
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "PaintBall");
        //TODO: this replaces the onMove thing in join signs, it runs twice a second and updates them
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Arena a : ArenaManager.getArenaManager().getArenas()) {
                    a.updateAllSigns();
                }
            }
        }, 10l, 10l);
        this.getCommand("paintball").setExecutor(commandManager);
    }

    @Override
    public void onDisable() {
        ArenaManager.getArenaManager().stopArenas();
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
                if (serverUUID.equalsIgnoreCase(this.getConfig().getString("ServerID"))) {
                    if (p != null) {
                        //TODO: add player to lobby of arena with name aname
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: bad way of getting player data, find better way
    public PlayerData getPlayerData() {
        return this.data;
    }
}