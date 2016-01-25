package me.synapz.paintball.countdowns;

import me.synapz.paintball.Paintball;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class PaintballCountdown extends BukkitRunnable {

    protected double decrement = 1;
    protected int end = 0;
    protected double counter;

    public PaintballCountdown(int counter) {
        this.counter = counter;
        this.runTaskTimer(JavaPlugin.getProvidingPlugin(Paintball.class), 0, 20);
    }

    public PaintballCountdown(double counter) {
        this.counter = counter;
        this.runTaskTimer(JavaPlugin.getProvidingPlugin(Paintball.class), 0, 20);
    }

    @Override
    public void run() {
        if (stop())
            cancel();

        if ((int) counter <= end) {
            onFinish();
            cancel();
        } else {
            if (intervalCheck())
                onIteration();
        }
        counter = counter - decrement;
    }

    // Called once the counter reaches 0
    public abstract void onFinish();

    // Called every iteration of run()
    public abstract void onIteration();

    // Checks that must be full-filled in order to run, if this is not met, then it will cancel
    public abstract boolean stop();

    // Some countdowns have an interval to do things (ex: every 15 seconds print hi). This checks if there is an interval (set return true for no interval)
    public abstract boolean intervalCheck();

    public void cancel() {
        super.cancel();
    }

    public double getCounter() {
        return counter;
    }
}
