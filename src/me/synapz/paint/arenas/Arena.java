package me.synapz.paint.arenas;


import me.synapz.paint.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Arena {

    private boolean isMaxSet;
    private boolean isMinSet;
    private boolean isRedSpawnSet;
    private boolean isBlueSpawnSet;
    private boolean isBlueLobbySet;
    private boolean isRedLobbySet;

    private boolean inProgress;
    private boolean isEnabled;

    private int max;
    private int min;

    private String name;
    private Location redSpawn, blueSpawn, redLobbySpawn, blueLobbySpawn;

    private HashMap<String, ArenaManager.Team> players = new HashMap<String, ArenaManager.Team>();
    private HashMap<String, ArenaManager.Team> lobbyPlayers = new HashMap<String, ArenaManager.Team>();

    public Arena(String name) {
        this.name = name;
        ArenaManager.getArenaManager().addArena(this);
    }

    public String getName() {
        return name;
    }

    public Location getSpawn(ArenaManager.Team team) {
        switch (team) {
            case RED:
                return redSpawn;
            case BLUE:
                return blueSpawn;
            default:
                return null;
        }
    }

    public void setArenaSpawn(Location location, ArenaManager.Team team) {
        switch (team) {
            case RED:
                isRedSpawnSet = true;
                redSpawn = location;
                break;
            case BLUE:
                isBlueSpawnSet = true;
                blueSpawn = location;
                break;
        }
    }

    public void setLobbySpawn(Location location, ArenaManager.Team team) {
        switch (team) {
            case BLUE:
                isBlueLobbySet = true;
                blueLobbySpawn = location;
                break;
            case RED:
                isRedLobbySet = true;
                redLobbySpawn = location;
                break;
            default:
                break;
        }
    }

    public Location getLobbySpawn(ArenaManager.Team team) {
        switch (team) {
            case BLUE:
                return blueLobbySpawn;
            case RED:
                return redLobbySpawn;
            default:
                return null;
        }
    }

    public void setMaxPlayers(int max) {
        isMaxSet = true;
        this.max = max;
    }

    public void setMinPlayers(int min) {
        isMinSet = true;
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public String getSteps() {
        String finalString = "";
        ChatColor done = ChatColor.STRIKETHROUGH;
        String end = ChatColor.RESET + "" + ChatColor.GREEN;

        String max = isMaxSet ? done + "setMax"+end : "setMax";
        String min = isMinSet ? done + "setMin"+end : "setMin";
        String redSpawn = isRedSpawnSet ? done + "redSpawn"+end : "redSpawn";
        String blueSpawn = isBlueSpawnSet ? done + "blueSpawn"+end : "blueSpawn";
        String redLobbySpawn = isRedLobbySet ? done + "redLobby"+end : "redLobby";
        String blueLobbySpawn = isBlueLobbySet ? done + "blueLobby"+end : "blueLobby";
        String[] steps = {max, min, redSpawn, blueSpawn, redLobbySpawn, blueLobbySpawn};

        for (String step : steps) {
            finalString = finalString + ", " + step;
        }

        return isSetup() ? "All steps are finished." : finalString.substring(2, finalString.length());
    }

    public void startGame(Player sender) {
        if (lobbyPlayers.keySet().size() <= this.getMax() && lobbyPlayers.keySet().size() >= this.getMin()) {
            for (String p : lobbyPlayers.keySet()) {
                Player player = Bukkit.getPlayer(p);
                this.addPlayerToArena(player);
            }
            inProgress = true;
        } else {
            Message.getMessenger().msg(sender, ChatColor.RED, "There are not enough players in the lobby to start.");
        }
    }

    public boolean isSetup() {
        return isMinSet && isMaxSet && isRedLobbySet && isRedSpawnSet && isBlueSpawnSet && isBlueLobbySet;
    }

    public boolean isEnable() {
        return isEnabled;
    }

    public void removePlayer(Player player) {
        lobbyPlayers.keySet().remove(player.getName());
        players.keySet().remove(player.getName());
        // player.teleport <getbacklocation from config>
    }

    public ArenaManager.Team getTeam(Player player) {
        return lobbyPlayers.get(player.getName());
    }

    public void joinArena(Player player, ArenaManager.Team team) {
        if (this.inProgress) {
            Message.getMessenger().msg(player, ChatColor.RED, "That arena is currently in progress!");
            return;
        }
        if (team == null) {
            lobbyPlayers.put(player.getName(), getTeamWithLessPlayers());
        } else {
            lobbyPlayers.put(player.getName(), team);
        }
        player.teleport(getLobbySpawn(getTeam(player)));
        Message.getMessenger().msg(player, ChatColor.GREEN, "Successfully joined arena " + this.getName() + "'s lobby.");

        if (lobbyPlayers.keySet().size() >= this.getMin() && lobbyPlayers.keySet().size() <= this.getMax()) {
            this.startGame(player);
        }
    }

    private void addPlayerToArena(Player player) {
        ArenaManager.Team team = this.getTeam(player);

        players.put(player.getName(), team);
        player.teleport(getSpawn(team));

        Message.getMessenger().msg(player, ChatColor.GREEN, "Arena " + this.getName() + " starting!");
    }

    public boolean containsPlayer(Player player) {
        return lobbyPlayers.keySet().contains(player.getName()) || players.keySet().contains(player.getName());
    }

    private ArenaManager.Team getTeamWithLessPlayers() {
        int red = 0, blue = 0;
        for (String p : players.keySet()) {
            if (players.get(p) == ArenaManager.Team.RED)
                red++;
            else
                blue++;
        }
        if (red > blue)
            return ArenaManager.Team.BLUE;
        else
            return ArenaManager.Team.RED;
    }


}
