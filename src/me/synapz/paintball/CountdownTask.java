package me.synapz.paintball;

import static org.bukkit.ChatColor.*;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class CountdownTask extends BukkitRunnable {

    // used to check if there is a current instance of CountdownTask running for an arena; we don't want double messages!
    public static ArrayList<Arena> arenasRunningTask = new ArrayList<Arena>();

    private Arena a; // arena for the countdown
    private int interval; // countdown interval
    private int noInterval; // countdown when there is no interval
    private String finishedMessage; // message when countdown is finished
    private boolean isLobbyCountdown; // if it is a lobbyCountdown this is true
    private String chatMessage; // message to be sent in chat
    private String screenMessage; // message to be sent in middle of screen using title api
    private boolean isSimpleTimer = false; // a simpleTimer is no messages, just counting
    private int counter;

    // Creates a new countdown
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

    public CountdownTask(Arena a, int counter) {
        this.a = a;
        this.counter = counter;
        isSimpleTimer = true;
    }

    @Override
    public void run() {
        if (isSimpleTimer) {
            if (counter <= 0) {
                ;
                this.cancel();
            } else {
                counter--;
            }
            return;
        }
        // TODO: check if this part works
        if (a.getLobbyPlayers().size() < a.getMin() && a.getState() != Arena.ArenaState.IN_PROGRESS) {
            // cancel it because people left and it doesnt have enough players
            arenasRunningTask.remove(a);
            this.cancel();
        }
        if (counter <= 0) {
            // countdown is finished...
            arenasRunningTask.remove(a);
            a.broadcastMessage(GREEN, finishedMessage, finishedMessage);
            if (isLobbyCountdown)
                a.startGame();
            new CountdownTask(a, a.TIME);
            this.cancel();
        } else {
            if (counter <= noInterval || counter % interval == 0) {
                // replace the messages and broadcast it, then set back the message to it's layout for next countdown message
                chatMessage = chatMessage.replace("%time%", counter+"");
                screenMessage = screenMessage.replace("%time%", counter+"");
                a.broadcastMessage(GREEN, chatMessage, screenMessage);
                chatMessage = chatMessage.replace(GRAY + "" + counter, GRAY + "%time%");
                screenMessage = screenMessage.replace(GRAY + "" + counter, GRAY + "%time%");
            }
        }
        counter--;
    }
}