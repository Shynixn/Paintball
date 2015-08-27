package me.synapz.paintball;


import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import sun.misc.Cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Arena {

    // TODO: In case a reload or retart happens, make it so ArenaState gets saved in Cache file.
    // todo: set back values after leaving
    private HashMap<PbPlayer, ArenaManager.Team> players = new HashMap<PbPlayer, ArenaManager.Team>();
    private HashMap<String, ArenaManager.Team> lobbyPlayers = new HashMap<String, ArenaManager.Team>();
    private ArrayList<String> spectators = new ArrayList<String>();
    private FileConfiguration file = Settings.getSettings().getArenaFile();

    private boolean isMaxSet, isMinSet, isRedSpawnSet, isBlueSpawnSet, isSpectateSet, isBlueLobbySet, isRedLobbySet, isEnabled;

    private String name, currentName;

    String maxPath = "Max-Players";
    String minPath = "Min-Players";
    String redSpawnPath = "Red-Spawn";
    String redLobbyPath = "Red-Lobby";
    String blueSpawnPath = "Blue-Spawn";
    String blueLobbyPath = "Blue-Lobby";
    String enabledPath = "Is-Enabled";
    String spectatePath = "Spectate-Loc";
    ArenaState state = ArenaState.NOT_SETUP;

    public enum ArenaState {
        NOT_SETUP,
        WAITING,
        DISABLED,
        IN_PROGRESS,
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
        return (Location) file.get(getPath() + (team == ArenaManager.Team.BLUE ? blueSpawnPath : redSpawnPath));
    }

    public void setArenaSpawn(Location location, ArenaManager.Team team) {
        switch (team) {
            case BLUE:
                isBlueSpawnSet = true;
                break;
            case RED:
                isRedSpawnSet = true;
                break;
        }

        file.set(getPath() + (team == ArenaManager.Team.BLUE ? blueSpawnPath : redSpawnPath), location);
        advSave();
    }

    public void setLobbySpawn(Location location, ArenaManager.Team team) {
        switch (team) {
            case BLUE:
                isBlueLobbySet = true;
                break;
            case RED:
                isRedLobbySet = true;
                break;
        }

        file.set(getPath() + (team == ArenaManager.Team.BLUE ? blueLobbyPath : redLobbyPath), location);
        advSave();
    }

    public Location getLobbySpawn(ArenaManager.Team team) {
        return (Location) file.get(getPath() + (team == ArenaManager.Team.BLUE ? blueLobbyPath : redLobbyPath));
    }

    public void setSpectateLoc(Location loc) {
        isSpectateSet = true;
        file.set(getPath() + spectatePath, loc);
        advSave();
    }

    private Location getSpectateLoc() {
        return (Location) file.get(getPath() + spectatePath);
    }

    public void joinSpectate(Player player) {
        Settings.getSettings().getCache().savePlayerInformation(player);
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

        String max = isMaxSet ? done + "max"+end : "max";
        String min = isMinSet ? done + "min"+end : "min";
        String redSpawn = isRedSpawnSet ? done + "red-spawn"+end : "red-spawn";
        String blueSpawn = isBlueSpawnSet ? done + "blue-spawn"+end : "blue-spawn";
        String redLobbySpawn = isRedLobbySet ? done + "red-lobby"+end : "red-lobby";
        String blueLobbySpawn = isBlueLobbySet ? done + "blue-lobby"+end : "blue-lobby";
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
                    state = ArenaState.WAITING;
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

    public boolean isSetup() {
        return isMaxSet && isMinSet && isRedSpawnSet && isBlueSpawnSet && isBlueLobbySet && isRedLobbySet && isSpectateSet;
    }

    public ArenaManager.Team getTeam(Player player) {
        return lobbyPlayers.get(player.getName());
    }

    public boolean containsPlayer(Player player) {
        return lobbyPlayers.keySet().contains(player.getName()) || getPbPlayer(player) != null && players.containsKey(getPbPlayer(player)) || spectators.contains(player.getName());
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
            state = ArenaState.WAITING;
        } else {
            if (!isEnabled && isSetup()) {
                state = ArenaState.DISABLED;
            }
        }
    }

    public void forceStart(Player sender) {
        String reason = "";

        if (isSetup()) {
            if (lobbyPlayers.keySet().size() >= getMin()) {
                if (state == ArenaState.WAITING) {
                    startGame();
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
        Message.getMessenger().msg(sender, ChatColor.RED, "Cannot force start " + name, this.toString() + ChatColor.RED + reason);
    }

    public void forceStop(Player sender) {
        if (state == ArenaState.IN_PROGRESS) {
            this.removePlayers();
            broadcastMessage(ChatColor.RED, this.toString() + ChatColor.RED + " has been force stopped.");
            // todo make config value to compleelty block all commands
            if (!this.containsPlayer(sender)) {
                Message.getMessenger().msg(sender, ChatColor.GREEN, this.toString() + " has been force stopped.");
            }
            return;
        }
        Message.getMessenger().msg(sender, ChatColor.RED, "Cannot force stop " + this.toString(), this.toString() + ChatColor.RED + " is not in progress.");
    }

    public String toString() {
        return "Arena " + ChatColor.GRAY + this.getName() + ChatColor.GREEN;
    }


    private String getPath() {
        return "Arenas." + name + ".";
    }


    public void joinLobby(Player player, ArenaManager.Team team) {
        lobbyPlayers.put(player.getName(), team == null ? getTeamWithLessPlayers() : team);

        Settings.getSettings().getCache().savePlayerInformation(player);
        Utils.removePlayerSettings(player);

        player.teleport(getLobbySpawn(getTeam(player)));
        broadcastMessage(ChatColor.GREEN, player.getName() + " has joined the arena! " + ChatColor.GRAY + this.lobbyPlayers.keySet().size() + "/" + this.getMax());

        if (canStart()) {
            this.startGame();
        }
    }

    private void startGame() {
        Utils.countdown(this, Settings.COUNTDOWN);
        for (String p : lobbyPlayers.keySet()) {
            this.addPlayerToArena(Bukkit.getPlayer(p));
        }
        state = ArenaState.IN_PROGRESS;
    }

    public void removePlayers() {
        for (String p : lobbyPlayers.keySet()) {
            leave(Bukkit.getPlayer(p));
        }
        for (String p : spectators) {
            leave(Bukkit.getPlayer(p));
        }
        for (PbPlayer p : players.keySet()) {
            leave(p.getPlayer());
        }
        state = ArenaState.WAITING;
    }

    public void leave(Player player) {
        players.keySet().remove(getPbPlayer(player));
        lobbyPlayers.keySet().remove(player.getName());
        spectators.remove(player.getName());
        Settings.getSettings().getCache().restorePlayerInformation(player.getUniqueId());

        if (players.keySet().size() == 1) {
            PbPlayer pbPlayer = (PbPlayer) players.keySet().toArray()[0];
            Settings.getSettings().getCache().restorePlayerInformation(pbPlayer.getPlayer().getUniqueId());
            win(getTeam((pbPlayer.getPlayer())));
            players.keySet().remove(pbPlayer);
            state = ArenaState.WAITING;
        } else if (players.keySet().isEmpty()) {
            // in case min players is set to 0, when a player leaves arena doesn't get reset
            state = ArenaState.WAITING;
        }
    }

    public void win(ArenaManager.Team team) {
        String winner = team == ArenaManager.Team.RED ? ChatColor.RED + "Red Team" : ChatColor.BLUE + "Blue Team";
        broadcastMessage(ChatColor.GREEN, "The " + winner + ChatColor.GREEN + " has won!");
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
        // lobbyPlayers.keySet().remove(player.getName());
    }

    public void broadcastMessage(ChatColor color, String...messages) {
        for (PbPlayer pbPlayer : players.keySet()) {
            for (String message : messages) {
                Bukkit.getServer().getPlayer(pbPlayer.getName()).sendMessage(Settings.getSettings().getPrefix() + color + message);
            }
        }
        //for (String name : lobbyPlayers.keySet()) {
          //  for (String message : messages) {
            //    Bukkit.getServer().getPlayer(name).sendMessage(Settings.getSettings().getPrefix() + color + message);
            //}
        //}
    }

    private ArenaManager.Team getTeamWithLessPlayers() {
        int red = 0, blue = 0;
        for (String p : lobbyPlayers.keySet()) {
            if (lobbyPlayers.get(p) == ArenaManager.Team.RED)
                red++;
            else
                blue++;
        }
        if (red > blue)
            return ArenaManager.Team.BLUE;
        else
            return ArenaManager.Team.RED;
    }

    private boolean canStart() {
        return lobbyPlayers.keySet().size() >= this.getMin() && lobbyPlayers.keySet().size() <= this.getMax();
    }

    public ArenaState getState() {
        return state;
    }

    private void advSave() {
        Settings.getSettings().saveArenaFile();
        /**
         * Because the saveArenaFile() method gets called every time a value is changed,
         * we also want to see if the arena is setup because if it is, Arena.NOT_SETUP should
         * be replaced with ArenaState.WAITING (or ArenaState.DISABLED) because the setup is complete.
         */
        if (isSetup() && isEnabled) {
            state = ArenaState.WAITING;
        } else if (isSetup() && !isEnabled) {
            state = ArenaState.DISABLED;
        }
    }
}
