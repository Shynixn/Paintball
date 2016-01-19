package me.synapz.paintball.countdowns;

import static org.bukkit.ChatColor.*;

import me.synapz.paintball.Arena;

import java.util.HashMap;
import java.util.Map;

public class ArenaCountdown extends PaintballCountdown {

    /*
    This Countdown class is responsible for lobby and arena countdowns
     */


    // used to check if there is a current instance of CountdownTask running for an arena; we don't want double messages!
    public static Map<Arena, ArenaCountdown> tasks = new HashMap<Arena, ArenaCountdown>();

    private Arena a; // arena for the countdown
    private int interval; // countdown interval
    private int noInterval; // countdown when there is no interval
    private String finishedMessage; // message when countdown is finished
    private boolean isLobbyCountdown; // if it is a lobbyCountdown this is true
    private String chatMessage; // message to be sent in chat
    private String screenMessage; // message to be sent in middle of screen using title api

    // Creates a new countdown
    public ArenaCountdown(int counter, int interval, int noInterval, Arena a, String chatMessage, String screenMessage, String finishedMessage, boolean isLobbyCountdown) {
        super(counter);
        this.a = a;
        this.counter = counter;
        this.interval = interval;
        this.noInterval = noInterval;
        this.chatMessage = chatMessage;
        this.screenMessage = screenMessage;
        this.finishedMessage = finishedMessage;
        this.isLobbyCountdown = isLobbyCountdown;

        tasks.put(a, this);
    }

    @Override
    public void onFinish() {
        a.broadcastMessage(GREEN, finishedMessage, finishedMessage);
        this.cancel();
        if (isLobbyCountdown) {
            a.startGame();
        } else {
            a.setState(Arena.ArenaState.IN_PROGRESS);
            new GameCountdown(a);
        }
    }

    @Override
    public void onIteration() {
        // replace the messages and broadcast it, then set back the message to it's layout for next countdown message
        chatMessage = chatMessage.replace("%time%", (int) counter+"");
        screenMessage = screenMessage.replace("%time%", (int) counter+"");
        a.broadcastMessage(GREEN, chatMessage, screenMessage);
        chatMessage = chatMessage.replace(GRAY + "" + (int) counter, GRAY + "%time%");
        screenMessage = screenMessage.replace(GRAY + "" + (int) counter, GRAY + "%time%");
    }

    @Override
    public boolean stop() {
        // Countdown will auto cancel if... size is lower than the min (someone left), the state is not in progress and not starting
        return (a.getLobbyPlayers().size() < a.getMin() && a.getState() != Arena.ArenaState.IN_PROGRESS && a.getState() != Arena.ArenaState.STARTING);
    }

    @Override
    public boolean intervalCheck() {
        return counter <= noInterval || counter % interval == 0;
    }

    // Overrides cancel so that it cancels the task AND removes the arena from the tasks
    @Override
    public void cancel() {
        super.cancel();
        tasks.remove(a); // TODO: this work?
    }
}