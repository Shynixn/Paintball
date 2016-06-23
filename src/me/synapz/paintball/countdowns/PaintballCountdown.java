package me.synapz.paintball.countdowns;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.arenas.Arena;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public abstract class PaintballCountdown extends BukkitRunnable {

    public static final Map<Arena,PaintballCountdown> tasks = new HashMap<>();

    protected double decrement = 1;
    protected int end = 0;
    protected double counter;
    protected Arena arena;

    public PaintballCountdown(Arena arena, double counter) {
        this(counter);
        this.arena = arena;

        tasks.put(arena, this);
    }

    public PaintballCountdown(double counter) {
        this.counter = counter;

        this.runTaskTimer(Paintball.getInstance(), 0, 20);
    }

    @Override
    public void run() {
        if (stop()) {
            cancel();
            return;
        }

        if (counter <= end) {
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
        counter = -1D;
        tasks.remove(arena, this);

        if (arena != null)
            arena.updateSigns();
    }

    public double getCounter() {
        return counter;
    }
}
