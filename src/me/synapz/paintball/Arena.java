package me.synapz.paintball;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Arena {

    // TODO: In case a reload or retart happens, make it so ArenaState gets saved in Cache file.
    // TODO: add spectate leave...

    private boolean isMaxSet, isMinSet, isRedSpawnSet, isBlueSpawnSet, isSpectateSet, isBlueLobbySet, isRedLobbySet, isEnabled;
    private String name, currentName;

    private HashMap<PbPlayer, ArenaManager.Team> players = new HashMap<>();
    private HashMap<String, ArenaManager.Team> lobbyPlayers = new HashMap<>();
    private ArrayList<String> spectators = new ArrayList<>();
    private FileConfiguration file = Settings.getSettings().getArenaFile();

    String maxPath = "Max-Players";
    String minPath = "Min-Players";
    String redSpawnPath = "Red-Spawn";
    String redLobbyPath = "Red-Lobby";
    String blueSpawnPath = "Blue-Spawn";
    String blueLobbyPath = "Blue-Lobby";
    String enabledPath = "Is-Enabled";
    String spectatePath = "Spectate-Loc";
    ArenaState state = ArenaState.NOT_SETUP;

    protected enum ArenaState {
        NOT_SETUP,
        STOPPED,
        DISABLED,
        IN_PROGRESS,
        IN_LOBBY;
    }

    public Arena(String name, String currentName) {
        this.currentName = currentName;
        this.name = name;
    }

    public String getName() {
        return currentName;
    }

    public String getDefaultName() {
        return name;
    }

    private void setName(String newName) {
        file.set(getPath() + "Name", newName);
        currentName = newName;
    }

    public void removeArena() {
        file.set("Arenas." + this.getDefaultName(), null);
        
    	List<String> newList = Settings.getSettings().getArenaFile().getStringList("Arena-List");
    	newList.remove(this.name+ ":" + this.currentName);
        Settings.getSettings().getArenaFile().set("Arena-List", newList);

        ArenaManager.getArenaManager().getArenas().remove(this);
        advSave();
    }
    
    public void rename(String newName) {
        // Rename the file in 'Arena-List' path
    	List<String> newList = Settings.getSettings().getArenaFile().getStringList("Arena-List");
        newList.set(newList.indexOf(this.getDefaultName() + ":" + this.getName()), this.getDefaultName() + ":" + newName);
        Settings.getSettings().getArenaFile().set("Arena-List", newList);

        // Rename the Name: path under it's default name & rename the name in memory
        setName(newName);

    	advSave();
    }

    private Location getSpawn(ArenaManager.Team team) {
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
        advSave();
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
        advSave();
    }

    public Location getLobbySpawn(ArenaManager.Team team) {
        String value = team == ArenaManager.Team.BLUE ? blueLobbyPath : redLobbyPath;
        return (Location) file.get(getPath() + value);
    }

    public void setSpectateLoc(Location loc) {
        isSpectateSet = true;
        file.set(getPath() + spectatePath, loc);
        advSave();
    }

    private Location getSpectateLoc() {
        return (Location) file.get(getPath() + spectatePath);
    }

    public void addToSpectate(Player player) {
        switch (getState()) {
            case IN_PROGRESS:
                Message.getMessenger().msg(player, ChatColor.GREEN, "Joining " + this.toString() + " spectate zone.");
            case NOT_SETUP:
                Message.getMessenger().msg(player, ChatColor.RED, this.toString() + ChatColor.RED + "has not been fully setup.");
                return;
            case DISABLED:
                Message.getMessenger().msg(player, ChatColor.RED, this.toString() + ChatColor.RED + " is disabled.");
                return;
            default:
                break;
        }
        player.teleport(getSpectateLoc());
        spectators.add(player.getName());
    }
    public void setMaxPlayers(int max) {
        isMaxSet = true;
        file.set(getPath() + maxPath, max);
        advSave();
    }

    public void setMinPlayers(int min) {
        isMinSet = true;
        file.set(getPath() + minPath, min);
        advSave();
    }

    public int getMax() {
        return file.getInt(getPath() + maxPath);
    }

    public int getMin() {
        return file.getInt(getPath() + minPath);
    }

    public String getSteps() {
        String finalString = "";
        ChatColor done = ChatColor.STRIKETHROUGH;
        String end = ChatColor.RESET + "" + ChatColor.GRAY;
        String prefix = ChatColor.BLUE + "Steps: ";

        String max = isMaxSet ? done + "setMax"+end : "setMax";
        String min = isMinSet ? done + "setMin"+end : "setMin";
        String redSpawn = isRedSpawnSet ? done + "redSpawn"+end : "redSpawn";
        String blueSpawn = isBlueSpawnSet ? done + "blueSpawn"+end : "blueSpawn";
        String redLobbySpawn = isRedLobbySet ? done + "redLobby"+end : "redLobby";
        String blueLobbySpawn = isBlueLobbySet ? done + "blueLobby"+end : "blueLobby";
        String spectate = isSpectateSet ? done + "spectate"+end : "spectate";
        String enabled = isEnabled ? done+"enable"+end : "enable";
        String[] steps = {max, min, spectate, redSpawn, blueSpawn, redLobbySpawn, blueLobbySpawn, enabled};

        for (String step : steps) {
            finalString = finalString + ", " + ChatColor.GRAY + step;
        }

        return isSetup() && isEnabled ? prefix + ChatColor.GRAY + "Complete. Arena is open!" : prefix + finalString.subSequence(2, finalString.length());

    }

    public void setEnabled(boolean setEnabled, Player sender) {
        String message;
        ChatColor color;

        if (setEnabled) {
            if (!isSetup()) {
                color = ChatColor.RED;
                message = ChatColor.RED + "has not been setup.";
                isEnabled = false;
            } else {
                if (!isEnabled) {
                    isEnabled = true;
                    state = ArenaState.STOPPED;
                    color = ChatColor.GREEN;
                    message = "has been enabled!";
                } else {
                    color = ChatColor.RED;
                    message = ChatColor.RED + "is already enabled.";
                }
            }
        } else {
            if (isEnabled) {
                isEnabled = false;
                state = ArenaState.DISABLED;
                color = ChatColor.GREEN;
                message = "has been disabled!";
            } else {
                color = ChatColor.RED;
                message = ChatColor.RED + "is already disabled.";
            }
        }
        file.set(getPath() + enabledPath, isEnabled);
        advSave();
        Message.getMessenger().msg(sender, color, this.toString() + " " + message);
    }

    public void startGame() {
        putPlayersIntoArena();
    }

    public boolean isSetup() {
        return isMaxSet && isMinSet && isRedSpawnSet && isBlueSpawnSet && isBlueLobbySet && isRedLobbySet && isSpectateSet;
    }


    public void removePlayer(Player player) {
        lobbyPlayers.keySet().remove(player.getName());
        players.keySet().remove(player.getName());
        spectators.remove(player.getName());
        // player.teleport <getbacklocation from config>
    }

    public ArenaManager.Team getTeam(Player player) {
        return lobbyPlayers.get(player.getName());
    }

    public void joinLobby(Player player, ArenaManager.Team team) {
        switch (getState()) {
            case IN_PROGRESS:
                Message.getMessenger().msg(player, ChatColor.RED, this.toString() + ChatColor.RED + " is currently in progress.");
                return;
            case NOT_SETUP:
                Message.getMessenger().msg(player, ChatColor.RED, this.toString() + ChatColor.RED + " has not been fully setup.");
                return;
            case DISABLED:
                Message.getMessenger().msg(player, ChatColor.RED, this.toString() + ChatColor.RED + " is disabled.");
                return;
            default:
            	break;
        }

        if (team == null) {
            lobbyPlayers.put(player.getName(), getTeamWithLessPlayers());
        } else {
            lobbyPlayers.put(player.getName(), team);
        }

        // maybe change their name based on the team they are on
        player.teleport(getLobbySpawn(getTeam(player)));
        broadcastMessage(ChatColor.GREEN, player.getName() + " has joined the arena! " + ChatColor.GRAY + this.lobbyPlayers.keySet().size() + "/" + this.getMax());

        if (lobbyPlayers.keySet().size() >= this.getMin() && lobbyPlayers.keySet().size() <= this.getMax()) {
            this.startGame();
        }
    }

    public boolean containsPlayer(Player player) {
        return lobbyPlayers.keySet().contains(player.getName()) || players.keySet().contains(player.getName());
    }

    public void loadValues(FileConfiguration file) {

        String[] paths = {"Red-Lobby", "Red-Spawn", "Blue-Lobby", "Blue-Spawn", "Max-Players", "Min-Players", "Is-Enabled", "Spectate-Loc"};
        int pathValue = 0;

        for (String value : paths) {
            boolean isSet = false;
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
                case 7:
                    isSpectateSet = isSet;
                    break;
            }
            pathValue++;
        }
        if (isSetup() && isEnabled) {
            state = ArenaState.STOPPED;
        } else {
            if (!isEnabled && isSetup()) {
                state = ArenaState.DISABLED;
            }
        }
    }

    public void forceStart(Player sender) {
        String reason = "";

        if (isSetup()) {
            if (lobbyPlayers.keySet().size() > getMin()) {
                if (state == ArenaState.STOPPED) {
                    putPlayersIntoArena();
                    Message.getMessenger().msg(sender, ChatColor.GREEN, "Successfully started " + this.toString());
                    return;
                } else if (state == ArenaState.IN_PROGRESS) {
                    reason = " is already in progress";
                }
            } else {
                reason = " does not have enough players.";
            }
        } else {
            reason = " has not been setup or enabled.";
        }
        Message.getMessenger().msg(sender, ChatColor.RED, "Cannot force start " + name, "Error: " + this.toString() + ChatColor.RED + reason);
    }

    public void forceStop(Player sender) {
        if (state == ArenaState.IN_PROGRESS) {
            this.removePlayersInArena();
            Message.getMessenger().msg(sender, ChatColor.GREEN, "Successfully stopped " + this.toString());
            return;
        }
        Message.getMessenger().msg(sender, ChatColor.RED, "Cannot force stop " + this.toString(), "Error: " + this.toString() + ChatColor.RED + " is not in progress.");
    }

    public String toString() {
        return "Arena " + ChatColor.GRAY + this.getName() + ChatColor.GREEN;
    }


    private String getPath() {
        return "Arenas." + name + ".";
    }

    private void putPlayersIntoArena() {
        for (String p : lobbyPlayers.keySet()) {
            Player player = Bukkit.getPlayer(p);
            this.addPlayerToArena(player);
        }
        state = ArenaState.IN_PROGRESS;
        // TODO: make a timer & add configurable time
        this.broadcastMessage(ChatColor.GREEN, "Arena starting in " + "15 seconds");
    }

    public void removePlayersInArena() {
        broadcastMessage(ChatColor.RED, this.toString() + ChatColor.RED + " has been force stopped.");
        for (String p : lobbyPlayers.keySet()) {
            Player player = Bukkit.getPlayer(p);
            getPbPlayer(player).leaveArena();
            Message.getMessenger().msg(player, ChatColor.RED, "You left " + this.toString());
        }
        state = ArenaState.STOPPED;
    }

    public PbPlayer getPbPlayer(Player player) {
        for (PbPlayer pbPlayer : players.keySet()) {
            if (pbPlayer.getName().equals(player.getName())) {
                return pbPlayer;
            }
        }
        return null;
    }

    private void addPlayerToArena(Player player) {
        PbPlayer pbPlayer = new PbPlayer(player, getTeam(player), this);
        ArenaManager.Team team = this.getTeam(player);
        players.put(pbPlayer, team);
        player.teleport(getSpawn(team));
    }

    private void broadcastMessage(ChatColor color, String...messages) {
        for (PbPlayer pbPlayer : players.keySet()) {
            for (String message : messages) {
                Bukkit.getServer().getPlayer(pbPlayer.getName()).sendMessage(Settings.getSettings().getPrefix() + color + message);
            }
        }
        for (String name : lobbyPlayers.keySet()) {
            for (String message : messages) {
                Bukkit.getServer().getPlayer(name).sendMessage(Settings.getSettings().getPrefix() + color + message);
            }
        }
    }

    private ArenaManager.Team getTeamWithLessPlayers() {
        int red = 0, blue = 0;
        for (PbPlayer p : players.keySet()) {
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

    protected ArenaState getState() {
        return state;
    }

    private void advSave() {
        Settings.getSettings().saveArenaFile();
        /**
         * Because the saveArenaFile() method gets called every time a value is changed,
         * we also want to see if the arena is setup because if it is, Arena.NOT_SETUP should
         * be replaced with ArenaState.STOPPED (or ArenaState.DISABLED) because the setup is complete.
         */
        if (isSetup() && isEnabled) {
            state = ArenaState.STOPPED;
        } else if (isSetup() && !isEnabled) {
            state = ArenaState.DISABLED;
        }
    }
}
