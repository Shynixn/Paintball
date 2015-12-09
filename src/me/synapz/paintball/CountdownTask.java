package me.synapz.paintball;

import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.ChatColor.GREEN;

public class CountdownTask extends BukkitRunnable {

    // TODO: bug... multiple arenas + static will make it not run two countdown tasks at the same time!!
    public static boolean hasTaskRunning;

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
    }


    // TODO: Find out why @Override is not allowed here and how to work around it?
    @Override
    public void run() {
        // TODO: If player leaves and joins fast they will get the countdown two times.
        // TODO: check if this part works
        if (a.getLobbyPlayers().size() < a.getMin() && a.getState() != Arena.ArenaState.IN_PROGRESS) {
            // cancel it because people left and it doesn't have enough players
            hasTaskRunning = false;
            this.cancel();
            return;
        }
        hasTaskRunning = true;
        if (counter <= 0) {
            hasTaskRunning = false;
            a.broadcastMessage(GREEN, finishedMessage, finishedMessage);
            if (isLobbyCountdown)
                a.startGame();
            this.cancel();
            return;
        } else {
            if (counter <= noInterval || counter % interval == 0) {
                chatMessage = chatMessage.replace("%time%", counter + "");
                screenMessage = screenMessage.replace("%time%", counter + "");
                a.broadcastMessage(GREEN, chatMessage, screenMessage);
                chatMessage = chatMessage.replace(counter + "", "%time%");
                screenMessage = screenMessage.replace(counter + "", "%time%");
            }
        }
        counter--;
    }
}
