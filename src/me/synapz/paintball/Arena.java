package me.synapz.paintball;


import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Arena {

    private ArrayList<Team> arenaTeamList = new ArrayList<Team>();
    private ArrayList<String> spectators = new ArrayList<String>();
    private FileConfiguration file = Settings.getSettings().getArenaFile();

    private boolean isMaxSet, isMinSet, isSpectateSet, isEnabled;

    private String name, currentName;

    String maxPath = "Max-Players";
    String minPath = "Min-Players";
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

        for (Team t : Team.TEAMS) {
            arenaTeamList.add(t);
        }
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
    	newList.remove(this.name + ":" + this.currentName);
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

    private Location getSpawn(Team team) {
        return (Location) file.get(getPath() + team.getTitleName() + ".Spawn");
    }

    public void setArenaSpawn(Location location, Team team) {
        file.set(getPath() + team.getTitleName() + ".Spawn", location);
        advSave();
    }

    public void setLobbySpawn(Location location, Team team) {
        file.set(getPath() + team.getTitleName() + ".Lobby", location);
        advSave();
    }

    public Location getLobbySpawn(Team team) {
        return (Location) file.get(getPath() + team.getTitleName() + ".Lobby");
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
        ArrayList<String> steps = new ArrayList<String>();
        String finalString = "";
        ChatColor done = ChatColor.STRIKETHROUGH;
        String end = ChatColor.RESET + "" + ChatColor.GRAY;
        String prefix = ChatColor.BLUE + "Steps: ";

        steps = Utils.addItemsToArray(steps, isMaxSet ? done + "max"+end : "max", isMinSet ? done + "min"+end : "min");
        for (Team t : arenaTeamList) {
            steps.add(file.get(getPath() + t.getTitleName() + ".Lobby") != null ? done + t.getTitleName().toLowerCase() + "-lobby" + end : t.getTitleName().toLowerCase() + "-lobby");
            steps.add(file.get(getPath() + t.getTitleName() + ".Spawn") != null ? done + t.getTitleName().toLowerCase() + "-spawn"+ end : t.getTitleName().toLowerCase() + "-spawn");
        }
        Utils.addItemsToArray(steps, isSpectateSet ? done + "spectate"+end : "spectate", isEnabled ? done+"enable"+end : "enable");

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
        boolean spawnsSet = true;
        for (Team t : arenaTeamList) {
            if (file.get(getPath() + t.getTitleName() + ".Lobby") == null) {
                spawnsSet = false;
            }
            if (file.get(getPath() + t.getTitleName() + ".Spawn")== null) {
                spawnsSet = false;
            }
        }
        return isMaxSet && isMinSet && isSpectateSet && spawnsSet;
    }

    public Team getTeam(Player player) {
        for (Team t : arenaTeamList) {
            if (t.getLobbyPlayers().contains(player.getName())) {
                return t;
            } else if (getPbPlayer(player) != null && t.getPlayersInArena().contains(getPbPlayer(player))) {
                return t;
            }
        }
        return null;
    }

    public boolean containsPlayer(Player player) {
        ArrayList<PbPlayer> players = new ArrayList<PbPlayer>();
        ArrayList<String> lobby = new ArrayList<String>();
        for (Team t : arenaTeamList) {
            for (PbPlayer pb : t.getPlayersInArena()) {
                players.add(pb);
            }
            for (String s : t.getLobbyPlayers()) {
                lobby.add(s);
            }
        }
        return lobby.contains(player.getName()) || getPbPlayer(player) != null && players.contains(getPbPlayer(player)) || spectators.contains(player.getName());
    }

    public void loadValues(FileConfiguration file) {
        String[] paths = {"Max-Players", "Min-Players", "Is-Enabled", "Spectate-Loc"};
        int pathValue = 0;

        for (String value : paths) {
            boolean isSet = false;
            if (!file.getString(getPath() + value).equals("not_set")) {
                isSet = true;
            }

            switch (pathValue) {
                case 0:
                    isMaxSet = isSet;
                    break;
                case 1:
                    isMinSet = isSet;
                    break;
                case 2:
                    isEnabled = file.getBoolean(getPath() + value);
                    break;
                case 3:
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
        int size = 0;
        for (Team t : arenaTeamList) {
            size += t.getLobbyPlayers().size();
        }
        if (isSetup()) {
            if (size >= getMin()) {
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


    public void joinLobby(Player player, Team team) {
        if (team == null)
            getTeamWithLessPlayers().addPlayerToLobby(player.getName());
        else
            team.addPlayerToLobby(player.getName());

        Settings.getSettings().getCache().savePlayerInformation(player);
        Utils.removePlayerSettings(player);

        player.teleport(getLobbySpawn(getTeam(player)));
        broadcastMessage(ChatColor.GREEN, getTeam(player).getChatColor() + player.getName() + ChatColor.GREEN + " has joined the arena! " + ChatColor.GRAY + getLobbySize() + "/" + this.getMax());

        if (canStart()) {
            this.startGame();
        }
    }

    private void startGame() {
        // Set all the player's walk speed, swim speed, and fly speed tpo 0

        Utils.countdown(this, Settings.COUNTDOWN, getAllPlayers());
        state = ArenaState.IN_PROGRESS;
        for (String p : getAllLobbyPlayers()) {
            this.addPlayerToArena(Bukkit.getPlayer(p));
        }
        // remove all lobby players and set them to ^^ players
        for (Team t : arenaTeamList) {
            t.getLobbyPlayers().removeAll(t.getLobbyPlayers());
        }
    }

    // Used for server reload and forcestops, so no messages will be sent
    public void removePlayers() {
        for (PbPlayer pb : getAllPlayers()) {
            Settings.getSettings().getCache().restorePlayerInformation(pb.getPlayer().getUniqueId());
        }
        for (String p : getAllLobbyPlayers()) {
            Settings.getSettings().getCache().restorePlayerInformation(Bukkit.getPlayer(p).getUniqueId());
        }
        // remove lobby players
        for (Team t : arenaTeamList) {
            t.removeAllLobbyPlayers();
        }
        // remove players
        for (Team t : arenaTeamList) {
            t.removeAllPlayers();
        }
        spectators.removeAll(spectators);
        state = ArenaState.WAITING;
    }

    public void leave(Player player) {
        Team team = getTeam(player);
        team.removePlayerInArena(getPbPlayer(player));
        team.removeLobbyPlayers(player.getName());
        spectators.remove(player.getName());
        Settings.getSettings().getCache().restorePlayerInformation(player.getUniqueId());

        if (getAllPlayers().size() == 1) {
            PbPlayer pbPlayer = (PbPlayer) getAllPlayers().toArray()[0];
            Settings.getSettings().getCache().restorePlayerInformation(pbPlayer.getPlayer().getUniqueId());
            win(getTeam((pbPlayer.getPlayer())));
            team.removePlayerInArena(pbPlayer);
            state = ArenaState.WAITING;
        }
        if (getAllPlayers().isEmpty()) {
            // in case min players is set to 0, when a player leaves arena doesn't get reset
            state = ArenaState.WAITING;
        }
    }

    public void win(Team team) {
        broadcastMessage(ChatColor.GREEN, "The " + team.getTitleName() + ChatColor.GREEN + " has won!");
    }

    public PbPlayer getPbPlayer(Player player) {
        for (PbPlayer pbPlayer : getAllPlayers()) {
            if (pbPlayer.getName().equals(player.getName())) {
                return pbPlayer;
            }
        }
        return null;
    }

    private void addPlayerToArena(Player player) {
        Team team = getTeam(player);
        PbPlayer pbPlayer = new PbPlayer(player, team, this);
        team.addPlayerInArena(pbPlayer);
        player.teleport(getSpawn(team));
    }

    public void broadcastMessage(ChatColor color, String...messages) {
        for (PbPlayer pb : getAllPlayers()) {
            for (String message : messages) {
                Bukkit.getServer().getPlayer(pb.getName()).sendMessage(Settings.getSettings().getPrefix() + color + message);
            }
        }
        for (String name : getAllLobbyPlayers()) {
            for (String message : messages) {
                Bukkit.getServer().getPlayer(name).sendMessage(Settings.getSettings().getPrefix() + color + message);
            }
        }
    }

    private Team getTeamWithLessPlayers() {
        // Make new HashMap with Team to Size, this way we can easily extract the largest size
        HashMap<Team, Integer> size = new HashMap<Team, Integer>();
        for (Team t : Team.TEAMS) {
            size.put(t, t.getLobbyPlayers().size());
        }
        return Utils.max(size);
    }

    private int getLobbySize() {
        int i = 0;
        for (Team t : arenaTeamList) {
            i += t.getLobbyPlayers().size();
        }
        return i;
    }

    private ArrayList<PbPlayer> getAllPlayers() {
        ArrayList<PbPlayer> players = new ArrayList<PbPlayer>();
        for (Team t : arenaTeamList) {
            for (PbPlayer pb : t.getPlayersInArena()) {
                players.add(pb);
            }
        }
        return players;
    }

    private ArrayList<String> getAllLobbyPlayers() {
        ArrayList<String> lobby = new ArrayList<String>();
        for (Team t : arenaTeamList) {
            for (String p : t.getLobbyPlayers()) {
                lobby.add(p);
            }
        }
        return lobby;
    }

    private boolean canStart() {
        int size = 0;
        for (Team t : arenaTeamList) {
            size += t.getLobbyPlayers().size();
        }
        return size >= this.getMin() && size <= this.getMax();
    }

    public ArenaState getState() {
        if (!isSetup()) {
            state = ArenaState.NOT_SETUP;
        }
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
