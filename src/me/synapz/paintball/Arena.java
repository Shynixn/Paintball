package me.synapz.paintball;


import com.connorlinfoot.titleapi.TitleAPI;
import com.google.common.base.Joiner;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.LobbyPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.players.SpectatorPlayer;
import org.bukkit.Bukkit;

import static me.synapz.paintball.storage.Settings.*;
import static org.bukkit.ChatColor.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class Arena {

    // Arena File so we can access and set arena settings
    private FileConfiguration FILE = getSettings().getArenaFile();

    // All the players in the arena (Lobby, Spec, InGame) linked to the player which is linked to the PaintballPLayer
    private Map<Player, PaintballPlayer> allPlayers = new HashMap<Player, PaintballPlayer>();
    private ArrayList<SpectatorPlayer> spectators = new ArrayList<SpectatorPlayer>();
    private List<LobbyPlayer> lobby = new ArrayList<LobbyPlayer>();
    private List<ArenaPlayer> inGame = new ArrayList<ArenaPlayer>();

    // Arenas values
    // TODO: possiblly remove these and just make a method checking if each max, min, spectator are set or if isEnabled is true
    private boolean isMaxSet, isMinSet, isSpectateSet, isEnabled;

    // Arena name is the current name of the arena
    // Arena currentName is the name set when setup, this is used for renaming: you can't change a path name so we keep the currentName for accessing paths
    private String defaultName, currentName;

    // Paths for easy access
    String maxPath = "Max-Players";
    String minPath = "Min-Players";
    String enabledPath = "Is-Enabled";
    String spectatePath = "Spectate-Loc";

    // Current state of the arena
    ArenaState state = ArenaState.NOT_SETUP;

    public enum ArenaState {
        NOT_SETUP,
        WAITING,
        DISABLED,
        IN_PROGRESS
    }

    /**
     * Creates a new arena
     * @param name Arenas current name, used to show the name of the arena
     * @param currentName Arenas name when setup, used to access paths
     */
    public Arena(String name, String currentName) {
        this.currentName = currentName;
        this.defaultName = name;

        // Go through each arena inside the arena (located in config) and add it to the team list
        for (Team t : ArenaManager.getArenaManager().getTeamsList(this)) {
            getArenaTeamList().add(t);
        }
    }

    // Returns the current name of the arena (Arenas.Syn.name)
    public String getName() {
        return this.currentName;
    }

    // Returns the path name of the arena ex: if path is Arenas.Syn it would return Syn
    public String getDefaultName() {
        return defaultName;
    }

    // Sets the current name of the
    private void setName(String newName) {
        FILE.set(getPath() + "Name", newName);
        this.currentName = newName;
    }

    // Removes arena from list and from arenas.yml
    public void removeArena() {
        FILE.set("Arenas." + this.getDefaultName(), null);
        ArenaManager.getArenaManager().getArenas().remove(this);
        advSave();
    }

    // Renames arena by setting Arenas.arena.name to the new name, however it keeps the past name as a way to reference in the path (can't change path names)
    public void rename(String newName) {
        // Rename .Name to arenas new name
        FILE.set(getPath() + "Name", newName);
        
        // Rename the name in memory
        setName(newName);
        advSave();
    }

    // Gets the lobby spawn of a team
    public Location getSpawn(Team team) {
        return (Location) FILE.get(team.getPath() + ".Spawn");
    }

    // Sets the spawn of a team in the arena to a location
    public void setArenaSpawn(Location location, Team team) {
        FILE.set(team.getPath() + ".Spawn", location);
        advSave();
    }

    // Sets the lobby of a team to a location
    public void setLobbySpawn(Location location, Team team) {
        FILE.set(team.getPath() + ".Lobby", location);
        advSave();
    }

    // Gets a team's lobby location
    public Location getLobbySpawn(Team team) {
        return (Location) FILE.get(team.getPath() + ".Lobby");
    }

    // Sets the spectator spawn for the arena
    public void setSpectateLoc(Location loc) {
        isSpectateSet = true;
        FILE.set(getPath() + spectatePath, loc);
        advSave();
    }

    // Gets the arena's spectator spawn
    public Location getSpectateSpawn() {
        return (Location) FILE.get(getPath() + spectatePath);
    }

    // Joins spectator (just creates a new SpectatorPlayer
    public void joinSpectate(Player player) {
        new SpectatorPlayer(this, player);
    }
    
    public void setMaxPlayers(int max) {
        isMaxSet = true;
        FILE.set(getPath() + maxPath, max);
        advSave();
    }

    // Sets the min required plauers to an int (lobby playercount reaches this number, it calls the lobby-countdown and waits for more players)
    public void setMinPlayers(int min) {
        isMinSet = true;
        FILE.set(getPath() + minPath, min);
        advSave();
    }

    // Gets the max number of players
    public int getMax() {
        return FILE.getInt(getPath() + maxPath);
    }

    // Gets the min number of players
    public int getMin() {
        return FILE.getInt(getPath() + minPath);
    }

    // Gets all of the teams on this arena
    public List<Team> getArenaTeamList() {
        return ArenaManager.getArenaManager().getTeamsList(this);
    }

    // Sets the teams of this arena
    public void setArenaTeamList(List<Team> teams) {
        // makes a list of teams
        List<String> teamColors = new ArrayList<String>();
        for (Team t : teams) {
            teamColors.add(t.getChatColor() + "");
        }
        // sets the arena's teams to the list of teams in arena.yml
        getSettings().getArenaFile().set(getPath() + "Teams", teamColors);
        advSave();
    }

    // Gets the steps for the arena
    // TODO: this is a giant block of code, see if there is any better ways of doing this
    public String getSteps() {
        ArrayList<String> steps = new ArrayList<String>();
        String finalString;
        ChatColor done = STRIKETHROUGH;
        String end = RESET + "" + GRAY;
        String prefix = BLUE + "Steps: ";
        
        steps = Utils.addItemsToArray(steps, isMaxSet ? done + "max"+end : "max", isMinSet ? done + "min"+end : "min");
        for (Team t : getArenaTeamList()) {
            String lobbyName = t.getTitleName().toLowerCase().replace(" ", "") + "-lobby";
            String spawnName = t.getTitleName().toLowerCase().replace(" ", "") + "-spawn";
            steps.add(FILE.get(t.getPath() + ".Lobby") != null ? done + lobbyName + end : lobbyName);
            steps.add(FILE.get(t.getPath() + ".Spawn") != null ? done + spawnName + end : spawnName);
        }
        Utils.addItemsToArray(steps, isSpectateSet ? done + "spectate" + end : "spectate", isEnabled ? done + "enable" + end : "enable", getArenaTeamList().isEmpty() ? "set-teams" : "");
        finalString = GRAY + Joiner.on(", ").join(steps);
        
        return isSetup() && isEnabled ? prefix + GRAY + "Complete. Arena is open!" : prefix + finalString;
        
    }

    // Set the arena to be enabled/disabled
    // TODO: do all the checks in Enable command class instead of here
    public void setEnabled(boolean setEnabled, Player sender) {
        String message;
        ChatColor color;
        
        if (setEnabled) {
            if (!isSetup()) {
                color = RED;
                message = RED + "has not been setup.";
                isEnabled = false;
            } else {
                if (!isEnabled) {
                    isEnabled = true;
                    state = ArenaState.WAITING;
                    color = GREEN;
                    message = "has been enabled!";
                } else {
                    color = RED;
                    message = RED + "is already enabled.";
                }
            }
        } else {
            if (isEnabled) {
                isEnabled = false;
                state = ArenaState.DISABLED;
                color = GREEN;
                message = "has been disabled!";
            } else {
                color = RED;
                message = RED + "is already disabled.";
            }
        }
        FILE.set(getPath() + enabledPath, isEnabled);
        advSave();
        Message.getMessenger().msg(sender, false, color, this.toString() + " " + message);
    }

    // Checks weather this arena is setup or not. In order to be setup max, min, spectator and all spawns must be set
    public boolean isSetup() {
        boolean spawnsSet = true;
        if (getArenaTeamList().isEmpty()) {
            spawnsSet = false;
        }
        for (Team t : getArenaTeamList()) {
            if (FILE.get(t.getPath() + ".Lobby") == null) {
                spawnsSet = false;
            }
            if (FILE.get(t.getPath() + ".Spawn")== null) {
                spawnsSet = false;
            }
        }
        return isMaxSet && isMinSet && isSpectateSet && spawnsSet;
    }

    // Check if a player is in the arena, all players have all spectator, lobby, and arena players
    public boolean containsPlayer(Player player) {
        return allPlayers.keySet().contains(player);
    }

    // Gets the PaintballPlayer hooked up to the player
    public PaintballPlayer getPaintballPlayer(Player player) {
        return allPlayers.get(player);
    }

    // Loads all the arenas values from arenas.yml into memory, sets isMinSet, isMaxSet, isEnabled, and isSpectateSet
    public void loadValues(FileConfiguration file) {
        String[] paths = {"Max-Players", "Min-Players", "Is-Enabled", "Spectate-Loc"};
        int pathValue = 0;
        
        for (String value : paths) {
            boolean isSet = false;
            if (!FILE.getString(getPath() + value).equals("not_set")) {
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

    // Force start this arena. It has to have enough player, be setup and enabled, and not be in progress
    // TODO: add all checks to ForceStart command class
    public void forceStart(Player sender) {
        // TODO: add ifs to command
        String reason = "";
        if (isSetup() || isEnabled) {
            if (lobby.size() >= getMin()) {
                if (state == ArenaState.WAITING) {
                    startGame();
                    Message.getMessenger().msg(sender, false, GREEN, "Successfully started " + this.toString());
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
        Message.getMessenger().msg(sender, false, RED, "Cannot force start " + this.toString(), this.toString() + RED + reason);
    }

    // Force stop the arena. It has to be in progress
    // TODO: add all checks into ForceStop command class
    public void forceStop(Player sender) {
        if (state == ArenaState.IN_PROGRESS) {
            this.forceRemovePlayers();
            broadcastMessage(RED, this.toString() + RED + " has been force stopped.", "");
            // todo make config value to compleelty block all commands
            if (!this.containsPlayer(sender)) {
                Message.getMessenger().msg(sender, false, GREEN, this.toString() + " has been force stopped.");
            }
            return;
        }
        Message.getMessenger().msg(sender, false, RED, "Cannot force stop " + this.toString(), this.toString() + RED + " is not in progress.");
    }

    // Put the arena to string ex: Arena Syn, where Syn is the arena name. At the end it is green so if you don't want it to be green change it after
    // TODO: make it have a param of ChatColor so we don't only have the end at green
    public String toString() {
        return "Arena " + GRAY + this.getName() + GREEN;
    }
    
    // Return the path the Arena is at with a . at the end (This is where defaultName comes in handy)
    public String getPath() {
        return "Arenas." + defaultName + ".";
    }
    
    public void joinLobby(Player player, Team team) {
        new LobbyPlayer(this, team == null ? getTeamWithLessPlayers() : team, player);
    }

    // Starts the game, turns all LobbyPlayer's into lobby players and t
    public void startGame() {
        // TODO: Set all the player's walk speed, swim speed, and fly speed tpo 0
        state = ArenaState.IN_PROGRESS;
        for (LobbyPlayer p : lobby) {
            new ArenaPlayer(this, p.getTeam(), p.getPlayer());
        }
        lobby.removeAll(lobby);
        // TODO does this work?
        allPlayers.remove(lobby);
        Utils.countdown(ARENA_COUNTDOWN, ARENA_INTERVAL, ARENA_NO_INTERVAL, this, "Paintball starting in " + GRAY + "%time%" + GREEN + " seconds!", GREEN + "Starting\n" + GRAY + "%time%" + GREEN + " seconds", GREEN + "Game started!", false);
    }
    
    // Used for server reload and forcestops, so no messages will be sent
    public void forceRemovePlayers() {
        for (PaintballPlayer player : allPlayers.values()) {
            player.leaveArena();
            // player.forceLeave()?
        }
        // TODO: keep this for now until forceRemove is full tested because if it doesn't work then this must be back
        /*
        if (inGame.keySet().isEmpty() && lobby.keySet().isEmpty() && spectators.isEmpty()) {
            state = ArenaState.WAITING;
            return;
        }
        for (PbPlayer pb : inGame.keySet()) {
            Team.getPluginScoreboard().getTeam(getTeam(pb.getPlayer()).getTitleName()).removePlayer(pb.getPlayer());
            Settings.getSettings().getCache().restorePlayerInformation(pb.getPlayer().getUniqueId());
        }
        for (Player p : lobby.keySet()) {
            Team.getPluginScoreboard().getTeam(getTeam(p).getTitleName()).removePlayer(p);
            Settings.getSettings().getCache().restorePlayerInformation(p.getUniqueId());
        }
        for (Player p : spectators) {
            Team.getPluginScoreboard().getTeam(getTeam(p).getTitleName()).removePlayer(p);
            Settings.getSettings().getCache().restorePlayerInformation(p.getUniqueId());
        }
        lobby.keySet().removeAll(lobby.keySet());
        inGame.keySet().removeAll(inGame.keySet());
        spectators.removeAll(spectators);
        state = ArenaState.WAITING; */
    }

    // Make a player leave the arena
    // TODO: Currently doesn't do anything, instead of this we want a leave inside the PaintballPlayer, keep this here to use for reference
    public void leave(Player player) {
        /*
        if (Team.getPluginScoreboard().getTeam(getTeam(player).getTitleName()) != null)     {
            Team.getPluginScoreboard().getTeam(getTeam(player).getTitleName()).removePlayer(player);
        }
        
        lobby.keySet().remove(player);
        inGame.keySet().remove(getPbPlayer(player));
        spectators.remove(player);
        Settings.getSettings().getCache().restorePlayerInformation(player.getUniqueId());
        
        if (inGame.keySet().size() == 1) {
            PbPlayer pbPlayer = (PbPlayer) inGame.keySet().toArray()[0];
            Team.getPluginScoreboard().getTeam(inGame.get(pbPlayer).getTitleName()).removePlayer(player);
            Settings.getSettings().getCache().restorePlayerInformation(pbPlayer.getPlayer().getUniqueId());
            win(getTeam((pbPlayer.getPlayer())));
            inGame.keySet().remove(getPbPlayer(player));
            state = ArenaState.WAITING;
        }
        */
    }

    // Called when a team wins
    // TODO: currently not used, add to a PAintballHitEven or something to check if they won then call this method
    public void win(Team team) {
        // TODO: add you won!
        broadcastMessage(GREEN, "The " + team.getTitleName() + GREEN + " has won!", "The " + team.getTitleName() + GREEN + " has won!");
    }

    // Broadcasts a message to the whole arena
    public void broadcastMessage(ChatColor color, String chatMessage, String screenMessage) {
        for (PaintballPlayer pbPlayer : allPlayers.values()) {
            if (!screenMessage.equals("") && TITLE_API) {
                String[] messages = screenMessage.split("\n");
                TitleAPI.sendTitle(pbPlayer.getPlayer(), 10, 10, 10, messages.length == 1 ? PREFIX : messages[0], messages.length == 1 ? screenMessage : messages[1]);
            }
            pbPlayer.getPlayer().sendMessage(PREFIX + color + chatMessage);
        }
    }

    // Returns the team with less players for when someone joins
    private Team getTeamWithLessPlayers() {
        // Make new HashMap with Team to Size, this way we can easily extract the largest size
        HashMap<Team, Integer> size = new HashMap<Team, Integer>();
        for (Team t : getArenaTeamList()) {
            // set the team size to 0
            size.put(t, 0);
            for (LobbyPlayer lobbyPlayer : lobby) {
                if (lobbyPlayer.getTeam().getTitleName().equals(t.getTitleName())) {
                    size.put(t, size.get(t)+1);
                }
            }
        }
        return Utils.max(this, size);
    }

    // If we can start the lobby timer
    public boolean canStartTimer() {
        int size = lobby.size();
        return size >= this.getMin() && size <= this.getMax();
    }

    // Gets the arenas current state
    public ArenaState getState() {
        if (!isSetup()) {
            state = ArenaState.NOT_SETUP;
        }
        return state;
    }

    // Returns the color associated with the state with it's name
    public String getStateAsString() {
        ChatColor color = RED;
        switch (state) {
            case DISABLED:
                color = GRAY;
                break;
            case WAITING:
                color = GREEN;
                break;
            case IN_PROGRESS:
                color = RED;
                break;
            case NOT_SETUP:
                color = GRAY;
                break;
        }
        return color + state.toString();
    }

    // Updates all join signs
    public void updateAllSigns() {
        List<String> signLocs = getSettings().getArenaFile().getStringList(getPath() + "Sign-Locs");
        if (signLocs == null) return;
        
        for (String s : signLocs) {
            String[] locStr = s.split(",");
            Location loc = new Location(Bukkit.getWorld(locStr[0]), Integer.parseInt(locStr[1]), Integer.parseInt(locStr[2]), Integer.parseInt(locStr[3]));
            if (loc.getBlock().getState() instanceof Sign) {
                Sign sign = (Sign) loc.getBlock().getState();
                sign.setLine(1, this.getName()); // in case they rename it
                sign.setLine(2, this.getStateAsString());
                sign.setLine(3, this.inGame.size() + this.lobby.size() + "/" + this.getMax());
                sign.update();
            } else {
                ArenaManager.getArenaManager().removeSignLocation(loc, this);
            }
        }
    }

    // Add a lobby player to the arena
    public void addLobbyPlayer(LobbyPlayer lobbyPlayer) {
        if (!allPlayers.values().contains(lobbyPlayer)) {
            allPlayers.put(lobbyPlayer.getPlayer(), lobbyPlayer);
        }
        lobby.add(lobbyPlayer);
    }

    // Add an arena player to the arena
    public void addArenaPlayer(ArenaPlayer arenaPlayer) {
        if (!allPlayers.values().contains(arenaPlayer)) {
            allPlayers.put(arenaPlayer.getPlayer(), arenaPlayer);
        }
        inGame.add(arenaPlayer);
    }

    // Add a spectator to the arena
    public void addSpectator(SpectatorPlayer spectatorPlayer) {
        if (!allPlayers.values().contains(spectatorPlayer)) {
            allPlayers.put(spectatorPlayer.getPlayer(), spectatorPlayer);
        }
        spectators.add(spectatorPlayer);
    }

    // Get the list of lobby players
    public List<LobbyPlayer> getLobbyPlayers() {
        return lobby;
    }

    // Get the list of arena players
    public List<ArenaPlayer> getAllArenaPlayers() {
        return inGame;
    }

    // Get the lsit of spectators
    public List<SpectatorPlayer> getSpectators() {
        return spectators;
    }

    // get the list of all players
    public Map<Player, PaintballPlayer> getAllPlayers() {
        return allPlayers;
    }

    // Saves arena file along with other checks
    private void advSave() {
        getSettings().saveArenaFile();
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