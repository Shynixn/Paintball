package me.synapz.paintball.arenas;


import me.synapz.paintball.Message;
import me.synapz.paintball.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
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

    private String name;

    private HashMap<String, ArenaManager.Team> players = new HashMap<String, ArenaManager.Team>();
    private HashMap<String, ArenaManager.Team> lobbyPlayers = new HashMap<String, ArenaManager.Team>();
    private FileConfiguration file = Settings.getSettings().getArenaFile();

    String maxPath = "Max-Players";
    String minPath = "Min-Players";
    String redSpawnPath = "Red-Spawn";
    String redLobbyPath = "Red-Lobby";
    String blueSpawnPath = "Blue-Spawn";
    String blueLobbyPath = "Blue-Lobby";
    String enabledPath = "Is-Enabled";

    public Arena(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Location getSpawn(ArenaManager.Team team) {
        String value = team == ArenaManager.Team.BLUE ? blueSpawnPath : redSpawnPath;
        return (Location) file.get(getPath() + value);
    }

    public void setArenaSpawn(Location location, ArenaManager.Team team) {
        String value = "";
        switch (team) {
            case BLUE:
                value = blueSpawnPath;
                isBlueSpawnSet = true;
                break;
            case RED:
                value = redSpawnPath;
                isRedSpawnSet = true;
                break;
        }

        file.set(getPath() + value, location);
        Settings.getSettings().saveArenaFile();
    }

    public void setLobbySpawn(Location location, ArenaManager.Team team) {
        String value = "";
        switch (team) {
            case BLUE:
                value = blueLobbyPath;
                isBlueLobbySet = true;
                break;
            case RED:
                value = redLobbyPath;
                isRedLobbySet = true;
                break;
        }

        file.set(getPath() + value, location);
        Settings.getSettings().saveArenaFile();
    }

    public Location getLobbySpawn(ArenaManager.Team team) {
        String value = team == ArenaManager.Team.BLUE ? blueLobbyPath : redLobbyPath;
        return (Location) file.get(getPath() + value);
    }

    public void setMaxPlayers(int max) {
        isMaxSet = true;
        file.set(getPath() + maxPath, max);
        Settings.getSettings().saveArenaFile();
    }

    public void setMinPlayers(int min) {
        isMinSet = true;
        file.set(getPath() + minPath, min);
        Settings.getSettings().saveArenaFile();
    }

    public int getMax() {
        return file.getInt(getPath() + maxPath);
    }

    public int getMin() {
        return file.getInt(getPath() + minPath);
    }

    public String getSteps() {
        // TODO: make finished arenas green (and if disabled green with a strikethrough)
        // TODO: make unfinished arenas gray with strikethrough
        String finalString = "";
        ChatColor done = ChatColor.STRIKETHROUGH;
        String end = ChatColor.RESET + "" + ChatColor.GRAY;
        String suffix = ChatColor.BLUE + "Steps: ";

        String max = isMaxSet ? done + "setMax"+end : "setMax";
        String min = isMinSet ? done + "setMin"+end : "setMin";
        String redSpawn = isRedSpawnSet ? done + "redSpawn"+end : "redSpawn";
        String blueSpawn = isBlueSpawnSet ? done + "blueSpawn"+end : "blueSpawn";
        String redLobbySpawn = isRedLobbySet ? done + "redLobby"+end : "redLobby";
        String blueLobbySpawn = isBlueLobbySet ? done + "blueLobby"+end : "blueLobby";
        String[] steps = {max, min, redSpawn, blueSpawn, redLobbySpawn, blueLobbySpawn};

        for (String step : steps) {
            finalString = finalString + ", " + ChatColor.GRAY + step;
        }

        return isSetup() ? suffix + ChatColor.GRAY + "Complete. Arena is now open!" : suffix + finalString.subSequence(2, finalString.length());
    }

    public void setEnabled(boolean setEnabled, Player sender) {
        String message = "";
        ChatColor color = null;

        if (setEnabled) {
            if (!isEnabled) {
                isEnabled = true;
                color = ChatColor.GREEN;
                message = "has been enabled!";
            } else {
                color = ChatColor.RED;
                message = "is already enabled.";
            }
        } else {
            if (isEnabled) {
                isEnabled = false;
                color = ChatColor.GREEN;
                message = "has been disabled!";
            } else {
                color = ChatColor.RED;
                message = "is already disabled.";
            }
        }
        file.set(getPath() + enabledPath, isEnabled);
        Settings.getSettings().saveArenaFile();
        Message.getMessenger().msg(sender, color, "Arena " + this.getName() + " " + message);
    }

    public void startGame(Player sender) {
        this.addPlayersToArena();
    }

    public boolean isSetup() {
        return isMinSet && isMaxSet && isRedLobbySet && isRedSpawnSet && isBlueSpawnSet && isBlueLobbySet && isEnabled;
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
        // Message.getMessenger.broadcast(name + "joined arena! 3/MAX players");

        if (lobbyPlayers.keySet().size() >= this.getMin() && lobbyPlayers.keySet().size() <= this.getMax()) {
            this.startGame(player);
        }
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

    public void loadValues(FileConfiguration file) {

        String[] paths = {"Red-Lobby", "Red-Spawn", "Blue-Lobby", "Blue-Spawn", "Max-Players", "Min-Players", "Is-Enabled"};
        int pathValue = 0;
        boolean isSet = false;

        for (String value : paths) {
            if (!file.getString(getPath() + value).equals("not_set")) {
                isSet = true;
            }

            switch (pathValue) {
                case 0:
                    isRedLobbySet = isSet;
                    break;
                case 1:
                    isRedSpawnSet = isSet;
                    break;
                case 2:
                    isBlueLobbySet = isSet;
                    break;
                case 3:
                    isBlueSpawnSet = isSet;
                    break;
                case 4:
                    isMaxSet = isSet;
                    break;
                case 5:
                    isMinSet = isSet;
                    break;
                case 6:
                    isEnabled = file.getBoolean(getPath() + value);
                    break;
            }
            pathValue++;
        }
    }

    public void forceStart(Player sender) {
        String reason;
        String name = "Arena " + this.getName();

        if (isSetup()) {
            if (lobbyPlayers.keySet().size() > getMin()) {
                if (!inProgress) {
                    addPlayersToArena();
                    return;
                }
                reason = name + " is already in progress";
            } else {
                reason = name + " does not have enough players.";
            }
        } else {
            reason = name + " has not been setup or enabled.";
        }
        Message.getMessenger().msg(sender, ChatColor.RED, "Cannot force start that arena.", "Error: " + reason);
    }

    public void forceStop(Player sender) {
        String reason;
        String name = "Arena " + this.getName();

        if (this.inProgress) {
            this.removePlayersInArena();
            return;
        } else {
            reason = "is not in progress!";
        }
        Message.getMessenger().msg(sender, ChatColor.RED, "Cannot force stop that arena.", "Error: " + reason);
    }


    private String getPath() {
        return "Arenas." + this.getName() + ".";
    }

    private void addPlayersToArena() {
        for (String p : lobbyPlayers.keySet()) {
            Player player = Bukkit.getPlayer(p);
            this.addPlayerToArena(player);
        }
        inProgress = true;
    }

    private void removePlayersInArena() {
        // broadcast "Arena has been force stopped
        for (String p : lobbyPlayers.keySet()) {
            Player player = Bukkit.getPlayer(p);
            this.removePlayerFromArena(player);
            Message.getMessenger().msg(player, ChatColor.RED, "Left Arena!");
        }
        inProgress = true;
    }

    private void addPlayerToArena(Player player) {
        ArenaManager.Team team = this.getTeam(player);

        players.put(player.getName(), team);
        player.teleport(getSpawn(team));

        Message.getMessenger().msg(player, ChatColor.GREEN, "Joined Arena!");
        Message.getMessenger().msg(player, ChatColor.GREEN, "Arena " + this.getName() + " starting!");
        // broadcast 'Arena Starting'
    }

    private void removePlayerFromArena(Player player) {

        players.remove(player);
        // player.teleport(last location);
    }


}
