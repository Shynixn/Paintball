package me.synapz.paintball;

import me.synapz.paintball.storage.Settings;
import static org.bukkit.ChatColor.*;
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
        /*
        // TODO: If player leaves and joins fast they will get the countdown two times.
        if (counter == 0) {
            a.broadcastMessage(GREEN, "Paintball Arena Started!");
            this.cancel();
        } else {
            // todo: add title API here too
            if (counter <= Settings.NO_INTERVAL || counter % Settings.INTERVAL == 0) {
                a.broadcastMessage(GREEN, "Paintball starting in " + GRAY + counter + GREEN + " seconds!");
            }
        }
        counter--;*/
    }
}
