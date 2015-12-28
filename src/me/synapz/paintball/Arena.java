package me.synapz.paintball;


import com.connorlinfoot.titleapi.TitleAPI;
import com.google.common.base.Joiner;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.LobbyPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.players.SpectatorPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;

import static me.synapz.paintball.storage.Settings.*;
import static org.bukkit.ChatColor.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Arena {

    public final int TIME = Settings.getSettings().getConfig().getInt("Arena-Settings.Arenas." + getDefaultName() + ".time", Settings.ARENA_TIME);
    public final int MAX_SCORE = Settings.getSettings().getConfig().getInt("Arena-Settings.Arenas." + getDefaultName() + ".max-score", Settings.MAX_SCORE);

    // Arena File so we can access and set arena settings
    private FileConfiguration FILE = getSettings().getArenaFile();

    // All the players in the arena (Lobby, Spec, InGame) linked to the player which is linked to the PaintballPLayer
    private Map<Player, PaintballPlayer> allPlayers = new HashMap<Player, PaintballPlayer>();
    private ArrayList<SpectatorPlayer> spectators = new ArrayList<SpectatorPlayer>();
    private List<LobbyPlayer> lobby = new ArrayList<LobbyPlayer>();
    private List<ArenaPlayer> inGame = new ArrayList<ArenaPlayer>();

    // Arenas values
    private boolean isMaxSet, isMinSet, isSpectateSet, isEnabled;

    // Arena name is the current name of the arena
    // Arena currentName is the name set when setup, this is used for renaming: you can't change a path name so we keep the currentName for accessing paths
    private String defaultName, currentName;
    private Map<Team, Integer> teams = new HashMap<>();

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
        for (Team team : ArenaManager.getArenaManager().getTeamsList(this)) {
            teams.put(team, 0);
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
        Settings.getSettings().removeArenaConfigSection(this);
        ArenaManager.getArenaManager().getArenas().remove(this.getDefaultName(), this);
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
    public Set<Team> getArenaTeamList() {
        return teams.keySet();
    }

    // Sets the teams of this arena
    public void setArenaTeamList(List<Team> teamsToAdd) {
        // makes a list of teams
        List<String> teamColors = new ArrayList<String>();
        this.teams = new HashMap<>();
        for (Team t : teamsToAdd) {
            teamColors.add(t.getChatColor() + "");
            this.teams.put(t, 0);
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
    public void setEnabled(boolean setEnabled) {
        if (setEnabled)
            state = ArenaState.WAITING;
        else
            state = ArenaState.DISABLED;
        isEnabled = setEnabled;
        FILE.set(getPath() + enabledPath, isEnabled);
        advSave();
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

    public boolean isEnabled() {
        return isEnabled;
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

    // Force start/stop this arena. It has to have enough player, be setup and enabled, and not be in progress
    public void forceStart(boolean toStart) {
        if (toStart) {
            // in case there are any current countdown tasks in that arena (lobby countdown) we want to stop it
            if (CountdownTask.tasks.get(this) != null)
                CountdownTask.tasks.get(this).cancel();
            startGame();
        } else {
            this.broadcastMessage(RED, this.toString() + RED + " has been force stopped.", "");
            this.forceRemovePlayers();
        }
    }

    // Put the arena to string ex: Arena Syn, where Syn is the arena name. At the end it is green so if you don't want it to be green change it after
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
        state = ArenaState.IN_PROGRESS;
        for (LobbyPlayer p : lobby) {
            allPlayers.remove(p.getPlayer(), p);
            new ArenaPlayer(this, p.getTeam(), p.getPlayer());
        }
        lobby.removeAll(lobby);
        Utils.countdown(ARENA_COUNTDOWN, ARENA_INTERVAL, ARENA_NO_INTERVAL, this, "Paintball starting in " + GRAY + "%time%" + GREEN + " seconds!", GREEN + "Starting\n" + GRAY + "%time%" + GREEN + " seconds", GREEN + "Game started!", false);
    }
    
    // Used for server reload and arena force stops, so no messages will be sent
    public void forceRemovePlayers() {
        for (PaintballPlayer player : allPlayers.values()) {
            player.leaveArena();
        }
    }

    // Called when a team wins
    public void win(Team team) {
        if (Settings.BROADCAST_WINNER) {
            // TODO: broadcast to server / network (except for those in game, they get a different message)
        }
        for (ArenaPlayer arenaPlayer : getAllArenaPlayers()) {
            if (arenaPlayer.getTeam() == team)
                arenaPlayer.setWon();
        }
        new BukkitRunnable() {
            // TODO: add counter timer looking at config
            int counter = 10;
            @Override
            public void run() {
                if (counter == 0) {
                    forceRemovePlayers();
                    this.cancel();
                } else {
                    counter--;
                }
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(Paintball.class), 0, 20);
        broadcastMessage(GREEN, "The " + team.getTitleName() + GREEN + " has won!", "The " + team.getTitleName() + GREEN + " has won!");
        resetTeamScores();
    }

    // Broadcasts a message to the whole arena
    public void broadcastMessage(ChatColor color, String chatMessage, String screenMessage) {
        for (PaintballPlayer pbPlayer : allPlayers.values()) {
            if (!screenMessage.equals("") && TITLE_API) {
                String[] messages = screenMessage.split("\n");
                // TODO: get better numbers for fad in out etc
                TitleAPI.sendTitle(pbPlayer.getPlayer(), 0, 10, 10, messages.length == 1 ? PREFIX : messages[0], messages.length == 1 ? screenMessage : messages[1]);
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

    public void resetTeamScores() {
        // TODO: does this work?
        for (Team team : getArenaTeamList()) {
            teams.replace(team, getTeamScore(team), 0);
        }
    }

    public int getTeamScore(Team team) {
        return teams.get(team);
    }

    public void incrementTeamScore(Team team) {
        teams.replace(team, teams.get(team), teams.get(team)+1);
    }

    // Gets the arenas current state
    public ArenaState getState() {
        if (!isSetup()) {
            state = ArenaState.NOT_SETUP;
        }
        return state;
    }

    // Set the arena's state
    public void setState(ArenaState state) {
        this.state = state;
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
                sign.setLine(3, (state == ArenaState.WAITING ? getLobbyPlayers().size() + "": state == ArenaState.IN_PROGRESS ? getAllArenaPlayers().size() + "" : "0") + "/" + this.getMax());
                sign.update();
            } else {
                ArenaManager.getArenaManager().removeSignLocation(loc, this);
            }
        }
    }

    public void removePlayer(PaintballPlayer pbPlayer) {
        allPlayers.remove(pbPlayer.getPlayer(), pbPlayer);
        lobby.remove(pbPlayer);
        inGame.remove(pbPlayer);
        spectators.remove(pbPlayer);
    }

    public void addPlayer(PaintballPlayer pbPlayer) {
        if (!allPlayers.values().contains(pbPlayer)) {
            allPlayers.put(pbPlayer.getPlayer(), pbPlayer);
        }

        if (pbPlayer instanceof LobbyPlayer) {
            lobby.add((LobbyPlayer) pbPlayer);
        } else if (pbPlayer instanceof ArenaPlayer) {
            inGame.add((ArenaPlayer) pbPlayer);
        } else if (pbPlayer instanceof SpectatorPlayer) {
            spectators.add((SpectatorPlayer) pbPlayer);
        }
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


    // TODO: add events so players can't teleport and stuff inside arena
}