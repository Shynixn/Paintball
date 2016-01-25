package me.synapz.paintball.countdowns;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Message;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoHitCountdown extends PaintballCountdown {

    public static Map<String, NoHitCountdown> godPlayers = new HashMap<>();

    private String name;
    private ArenaPlayer arenaPlayer;
    private Player player;

    public NoHitCountdown(int counter, ArenaPlayer player) {
        super(counter);
        end = 1;
        this.name = player.getPlayer().getName();
        this.arenaPlayer = player;
        this.player = player.getPlayer();

        Message.getMessenger().msg(this.player, true, ChatColor.GREEN, Settings.SECONDARY + "You are being protected for " + Settings.THEME + counter + Settings.SECONDARY + " seconds!");
        if (!godPlayers.keySet().contains(name)) {
            godPlayers.put(name, this);
        }
    }

    public void onFinish() {
        godPlayers.remove(name, this);
        Message.getMessenger().msg(this.player, true, ChatColor.GREEN, "Your protection has expired!");
    }

    // Called every iteration of run()
    public void onIteration() {

    }

    public boolean stop() {
        return (player == null || arenaPlayer == null || arenaPlayer.getArena() == null || arenaPlayer.getArena() != null && arenaPlayer.getArena().getState() != Arena.ArenaState.IN_PROGRESS);
    }

    public boolean intervalCheck() {
        return true;
    }

    public void cancel() {
        super.cancel();
        godPlayers.remove(name, this);
    }
}
