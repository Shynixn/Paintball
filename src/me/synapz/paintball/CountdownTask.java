package me.synapz.paintball;

import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class CountdownTask extends BukkitRunnable {

    private final Arena a;
    private final Set<PbPlayer> players;

    private int counter;

    public CountdownTask(int counter, Arena a, Set<PbPlayer> pbPlayerList) {
        this.a = a;
        this.players = pbPlayerList;
        this.counter = counter;
    }

    public void run() {
        if (counter == 0) {
            a.broadcastMessage(ChatColor.GREEN, "Paintball Arena Started!");
            for (PbPlayer pb : players) {
                Utils.setAllSpeeds(pb.getPlayer(), 0.5F);
            }
            this.cancel();
        } else {
            if (counter <= Settings.NO_INTERVAL || counter % Settings.INTERVAL == 0) {
                a.broadcastMessage(ChatColor.GREEN, "Paintball starting in " + ChatColor.GRAY + counter + ChatColor.GREEN + " seconds!");
            }
        }
        counter--;
    }
}
