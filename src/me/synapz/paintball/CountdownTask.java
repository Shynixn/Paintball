package me.synapz.paintball;

import me.synapz.paintball.storage.Settings;
import static org.bukkit.ChatColor.*;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Set;

public class CountdownTask extends BukkitRunnable {

    // used to check if there is a current instance of CountdownTask running for an arena; we don't want double messages!
    public static ArrayList<Arena> arenasRunningTask = new ArrayList<Arena>();

    private final Arena a;
    private final int interval;
    private final int noInterval;
    private final String finishedMessage;
    private final boolean isLobbyCountdown;
    private String chatMessage;
    private String screenMessage;

    private int counter;

    public CountdownTask(int counter, int interval, int noInterval, Arena a, String chatMessage, String screenMessage, String finishedMessage, boolean isLobbyCountdown) {
        this.a = a;
        this.counter = counter;
        this.interval = interval;
        this.noInterval = noInterval;
        this.chatMessage = chatMessage;
        this.screenMessage = screenMessage;
        this.finishedMessage = finishedMessage;
        this.isLobbyCountdown = isLobbyCountdown;

        arenasRunningTask.add(a);
    }

    @Override
    public void run() {
        // TODO: If player leaves and joins fast they will get the countdown two times.
        // TODO: check if this part works
        if (a.getLobbyPlayers().size() < a.getMin() && a.getState() != Arena.ArenaState.IN_PROGRESS) {
            // cancel it because people left and it doesnt have enough players
            arenasRunningTask.remove(a);
            this.cancel();
        }
        if (counter <= 0) {
            arenasRunningTask.remove(a);
            a.broadcastMessage(GREEN, finishedMessage, finishedMessage);
            if (isLobbyCountdown)
                a.startGame();
            this.cancel();
        } else {
            if (counter <= noInterval || counter % interval == 0) {
                // replace the messages and broadcast it, then set back the message to it's layout
                chatMessage = chatMessage.replace("%time%", counter+"");
                screenMessage = screenMessage.replace("%time%", counter+"");
                a.broadcastMessage(GREEN, chatMessage, screenMessage);
                chatMessage = chatMessage.replace(counter+"", "%time%");
                screenMessage = screenMessage.replace(counter+"", "%time%");
            }
        }
        counter--;
    }
}
