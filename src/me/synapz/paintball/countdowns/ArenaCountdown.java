package me.synapz.paintball.countdowns;

import static org.bukkit.ChatColor.*;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Team;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

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
    private HashMap<Player, Location> startLocations;

    // Creates a new countdown
    public ArenaCountdown(int counter, int interval, int noInterval, Arena a, String chatMessage, String screenMessage, String finishedMessage, HashMap<Player, Location> startLocations, boolean isLobbyCountdown) {
        super(counter);
        this.a = a;
        this.counter = counter;
        this.interval = interval;
        this.noInterval = noInterval;
        this.chatMessage = chatMessage;
        this.screenMessage = screenMessage;
        this.finishedMessage = finishedMessage;
        this.isLobbyCountdown = isLobbyCountdown;
        this.startLocations = startLocations;

        tasks.put(a, this);
    }

    @Override
    public void onFinish() {
        a.broadcastMessage(GREEN, finishedMessage, finishedMessage);
        this.cancel();
        if (isLobbyCountdown) {
            a.startGame();
        } else {
            tpAllPlayersBack();
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
        return (a == null || a != null && (a.getState() != Arena.ArenaState.IN_PROGRESS && a.getState() != Arena.ArenaState.STARTING && a.getState() != Arena.ArenaState.WAITING) || isLobbyCountdown && (a.getLobbyPlayers().size() < a.getMin()));
    }

    @Override
    public boolean intervalCheck() {
        if (!isLobbyCountdown) {
            tpAllPlayersBack(); // this method will get called every second since intervalCheck is called every second
        }
        return counter <= noInterval || counter % interval == 0;
    }

    // Overrides cancel so that it cancels the task AND removes the arena from the tasks
    @Override
    public void cancel() {
        super.cancel();
        tasks.remove(a, this);
    }

    private void tpAllPlayersBack() {
        for (ArenaPlayer arenaPlayer : a.getAllArenaPlayers()) {
            Location playerLoc = arenaPlayer.getPlayer().getLocation();
            Location spawnLoc = startLocations.get(arenaPlayer.getPlayer());

            int playerX = playerLoc.getBlockX();
            int playerZ = playerLoc.getBlockZ();
            int playerY = playerLoc.getBlockY();
            int spawnX = spawnLoc.getBlockX();
            int spawnY = spawnLoc.getBlockY();
            int spawnZ = spawnLoc.getBlockZ();

            if (playerX != spawnX || playerY != spawnY || playerZ != spawnZ) {
                arenaPlayer.getPlayer().teleport(spawnLoc);
            }
        }
    }
}