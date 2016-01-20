package me.synapz.paintball;


import com.connorlinfoot.titleapi.TitleAPI;
import com.google.common.base.Joiner;
import me.synapz.paintball.countdowns.ArenaCountdown;
import me.synapz.paintball.countdowns.GameFinishCountdown;
import me.synapz.paintball.countdowns.PaintballCountdown;
import me.synapz.paintball.locations.SignLocation;
import me.synapz.paintball.locations.SpectatorLocation;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.LobbyPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.players.SpectatorPlayer;
import me.synapz.paintball.storage.Settings;

import static me.synapz.paintball.locations.TeamLocation.*;
import static me.synapz.paintball.storage.Settings.*;
import static org.bukkit.ChatColor.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.*;

public class Arena {

    public int MAX_SCORE;
    public int TIME;
    public int WIN_WAIT_TIME;
    public int ARENA_COUNTDOWN;
    public int ARENA_INTERVAL;
    public int ARENA_NO_INTERVAL;
    public int LOBBY_COUNTDOWN;
    public int LOBBY_INTERVAL;
    public int LOBBY_NO_INTERVAL;
    public int KILLCOIN_PER_KILL;
    public int KILLCOIN_PER_DEATH;
    public int MONEY_PER_KILL;
    public int MONEY_PER_DEATH;
    public int MONEY_PER_WIN;
    public int MONEY_PER_DEFEAT;
    public int SAFE_TIME;

    public String ARENA_CHAT;
    public String SPEC_CHAT;

    public boolean BROADCAST_WINNER;
    public boolean PER_TEAM_CHAT_LOBBY;
    public boolean PER_TEAM_CHAT_ARENA;
    public boolean KILL_COIN_SHOP;
    public boolean GIVE_WOOL_HELMET_ARENA;
    public boolean GIVE_WOOL_HELMET_LOBBY;
    public boolean COLOR_PLAYER_TITLE_LOBBY;
    public boolean COLOR_PLAYER_TITLE_ARENA;
    public boolean GIVE_TEAM_SWITCHER;
    public boolean USE_ARENA_CHAT;

    // All the players in the arena (Lobby, Spec, InGame) linked to the player which is linked to the PaintballPLayer
    private Map<Player, PaintballPlayer> allPlayers = new HashMap<Player, PaintballPlayer>();
    private ArrayList<SpectatorPlayer> spectators = new ArrayList<SpectatorPlayer>();
    private List<LobbyPlayer> lobby = new ArrayList<LobbyPlayer>();
    private List<ArenaPlayer> inGame = new ArrayList<ArenaPlayer>();

    // Arena name is the current name of the arena
    // Arena currentName is the name set when setup, this is used for renaming: you can't change a path name so we keep the currentName for accessing paths
    private String defaultName, currentName;
    // Team with their size
    private Map<Team, Integer> teams = new HashMap<>();

    // Current state of the arena
    private ArenaState state = ArenaState.NOT_SETUP;

    private Map<Location, SignLocation> signLocations = new HashMap<>();

    public enum ArenaState {
        NOT_SETUP,
        WAITING,
        DISABLED,
        STARTING,
        IN_PROGRESS;

        @Override
        public String toString() {
            return super.toString().toLowerCase().replace("_", " ").replace(super.toString().toLowerCase().toCharArray()[0], super.toString().toUpperCase().toCharArray()[0]);
        }
    }

    /**
     * Creates a new arena
     * @param name Arenas current name, used to show the name of the arena
     * @param currentName Arenas name when setup, used to access paths
     */
    public Arena(String name, String currentName, boolean addToConfig) {
        this.currentName = currentName;
        this.defaultName = name;

        if (addToConfig) {
            Settings.ARENA.addNewArenaToFile(this);
        }

        loadConfigValues();
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
        ARENA_FILE.set(getPath() + "Name", newName);
        this.currentName = newName;
    }

    // Removes arena from list and from arenas.yml
    public void removeArena() {
        ARENA_FILE.set("Arenas." + this.getDefaultName(), null);
        Settings.getSettings().removeArenaConfigSection(this);
        ArenaManager.getArenaManager().getArenas().remove(this.getDefaultName(), this);
        advSave();
    }

    // Renames arena by setting Arenas.arena.name to the new name, however it keeps the past name as a way to reference in the path (can't change path names)
    public void rename(String newName) {
        // Rename .Name to arenas new name
        ARENA_FILE.set(getPath() + "Name", newName);
        
        // Rename the name in memory
        setName(newName);
        advSave();
    }

    // Gets the spawn of a team
    public Location getLocation(TeamLocations type, Team team) {
        return new TeamLocation(this, team, type).getLocation();
    }

    // Sets the spawn of a team in the arena to a location
    public void setLocation(TeamLocations type, Location location, Team team) {
        new TeamLocation(this, team, location, type);
    }

    public Location getSpectatorLocation() {
        return new SpectatorLocation(this).getLocation();
    }

    public void setSpectatorLocation(Location location) {
        new SpectatorLocation(this, location);
    }

    // Joins spectator (just creates a new SpectatorPlayer
    public void joinSpectate(Player player) {
        new SpectatorPlayer(this, player);
    }
    
    public void setMaxPlayers(int max) {
        ARENA_FILE.set(getPath() + "Max", max);
        advSave();
    }

    // Sets the min required plauers to an int (lobby playercount reaches this number, it calls the lobby-countdown and waits for more players)
    public void setMinPlayers(int min) {
        ARENA_FILE.set(getPath() + "Min", min);
        advSave();
    }

    public boolean isMinSet() {
        return getMin() != 0;
    }

    private boolean isMaxSet() {
        return getMax() != 0;
    }

    // Gets the max number of players
    public int getMax() {
        return ARENA_FILE.getInt(getPath() + "Max");
    }

    // Gets the min number of players
    public int getMin() {
        return ARENA_FILE.getInt(getPath() + "Min");
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
        ARENA_FILE.set(getPath() + "Teams", teamColors);
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
        
        steps = Utils.addItemsToArray(steps, isMaxSet() ? done + "max"+end : "max", isMinSet() ? done + "min"+end : "min");
        for (Team t : getArenaTeamList()) {
            String lobbyName = t.getTitleName().toLowerCase().replace(" ", "") + " (lobby)";
            String spawnName = t.getTitleName().toLowerCase().replace(" ", "") + " (spawn)";
            steps.add(ARENA_FILE.getString(t.getPath(TeamLocations.LOBBY)) != null ? done + lobbyName + end : lobbyName);
            steps.add(ARENA_FILE.getString(t.getPath(TeamLocations.SPAWN)) != null ? done + spawnName + end : spawnName);
        }
        Utils.addItemsToArray(steps, (ARENA_FILE.getString(this.getPath() + "Spectator") != null ? done + "setspec" + end : "setspec"), isEnabled() ? done + "enable" + end : "enable", getArenaTeamList().isEmpty() ? "setteams" : "");
        finalString = GRAY + Joiner.on(", ").join(steps);
        
        return isSetup() && isEnabled() ? prefix + GRAY + "Complete. Arena is open!" : prefix + finalString;
        
    }

    // Set the arena to be enabled/disabled
    public void setEnabled(boolean setEnabled) {
        if (setEnabled)
            state = ArenaState.WAITING;
        else
            state = ArenaState.DISABLED;
        ARENA_FILE.set(getPath() + "Enabled", setEnabled);
        advSave();
    }

    // Checks weather this arena is setup or not. In order to be setup max, min, spectator and all spawns must be set
    public boolean isSetup() {
        boolean spawnsSet = true;
        boolean isSpectateSet = ARENA_FILE.getString(this.getPath() + "Spectator") != null;
        if (getArenaTeamList().isEmpty()) {
            spawnsSet = false;
        }
        for (Team t : getArenaTeamList()) {
            if (ARENA_FILE.getString(t.getPath(TeamLocations.SPAWN)) == null) {
                spawnsSet = false;
            }
            // TODO make t.getPath(type) instead of boolean
            if (ARENA_FILE.getString(t.getPath(TeamLocations.LOBBY))== null) {
                spawnsSet = false;
            }
        }
        return isMaxSet() && isMinSet() && isSpectateSet && spawnsSet;
    }

    public boolean isEnabled() {
        return ARENA_FILE.getBoolean(this.getPath() + "Enabled");
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
    public void loadValues() {
        //  Puts all the Signs from arenas.yml into memory
        for (String rawLocation : ARENA_FILE.getStringList(this.getPath() + "Join")) {
            SignLocation signLoc = new SignLocation(this, rawLocation);
            signLocations.put(signLoc.getLocation(), signLoc);
        }

        setArenaTeamList(ARENA.getTeamsList(this));
        // TODO: add things from config.yml like time etc if they aren't defaulted
        if (isSetup() && isEnabled()) {
            state = ArenaState.WAITING;
        } else {
            if (!isEnabled() && isSetup()) {
                state = ArenaState.DISABLED;
            }
        }
    }

    public void updateSigns() {
        String prefix = DARK_GRAY + "[" + THEME + "Paintball" + DARK_GRAY + "]";
        for (SignLocation signLoc : getSignLocations().values()) {
            Location loc = signLoc.getLocation();
            if (!(loc.getBlock().getState() instanceof Sign)) {
                signLoc.removeSign();
                return;
            }
            Sign sign = (Sign) loc.getBlock().getState();

            sign.setLine(0, prefix); // in case the prefix changes
            sign.setLine(1, getName()); // in case they rename it
            sign.setLine(2, getStateAsString());
            sign.setLine(3, (getState() == Arena.ArenaState.WAITING ? getLobbyPlayers().size() + "": getState() == ArenaState.IN_PROGRESS || getState() == ArenaState.STARTING ? getAllArenaPlayers().size() + "" : "0") + "/" + getMax());
            sign.update();
        }
    }

    public Map<Location, SignLocation> getSignLocations() {
        return signLocations;
    }

    public void addSignLocation(SignLocation signLoc) {
        signLocations.put(signLoc.getLocation(), signLoc);
    }

    public void removeSignLocation(SignLocation signLoc) {
        signLocations.remove(signLoc.getLocation(), signLoc);
    }

    // Force start/stop this arena. It has to have enough player, be setup and enabled, and not be in progress
    public void forceStart(boolean toStart) {
        if (toStart) {
            // in case there are any current countdown tasks in that arena (lobby countdown) we want to stop it
            if (ArenaCountdown.tasks.get(this) != null)
                ArenaCountdown.tasks.get(this).cancel();
            startGame();
        } else {
            state = ArenaState.WAITING;
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

    public String getConfigPath(String item) {
        return "Per-Arena-Settings.Arenas." + defaultName + "." + item;
    }

    public String getDefaultConfigPath(String item) {
        return "Per-Arena-Settings.Defaults." + item;
    }
    
    public void joinLobby(Player player, Team team) {
        new LobbyPlayer(this, team == null ? getTeamWithLessPlayers() : team, player);
    }

    // Starts the game, turns all LobbyPlayers into ArenaPlayers
    public void startGame() {
        state = ArenaState.STARTING;
        for (LobbyPlayer p : lobby) {
            allPlayers.remove(p.getPlayer(), p);
            new ArenaPlayer(this, p.getTeam(), p.getPlayer());
        }
        lobby.removeAll(lobby);
        new ArenaCountdown(ARENA_COUNTDOWN, ARENA_INTERVAL, ARENA_NO_INTERVAL, this, "Paintball starting in " + GRAY + "%time%" + GREEN + " seconds!", GREEN + "Starting\n" + GRAY + "%time%" + GREEN + " seconds", GREEN + "Game started!", false);
    }
    
    // Used for server reload and arena force stops, so no messages will be sent
    public void forceRemovePlayers() {
        state = ArenaState.WAITING;
        for (PaintballPlayer player : allPlayers.values()) {
            player.forceLeaveArena();
        }
        allPlayers = new HashMap<>();
        lobby = new ArrayList<>();
        spectators = new ArrayList<>();
        inGame = new ArrayList<>();
        this.resetTeamScores();
    }

    // Called when a team wins
    public void win(Team team) {
        if (BROADCAST_WINNER) {
            // TODO: broadcast to server / network (except for those in game, they get a different message)
        }
        for (ArenaPlayer arenaPlayer : getAllArenaPlayers()) {
            if (arenaPlayer.getTeam() == team)
                arenaPlayer.setWon();
            Message.getMessenger().msg(arenaPlayer.getPlayer(), false, ChatColor.GREEN, (arenaPlayer.getMoneyEarned() < 0 ? "-" : "") + "$" + Math.abs(arenaPlayer.getMoneyEarned()), "Kills: " + arenaPlayer.getKills(), "Deaths: " + arenaPlayer.getDeaths(), "Killstreak: " + arenaPlayer.getKillStreak(), "KD: " + arenaPlayer.getKd(), "Your team " + (arenaPlayer.getTeam() == team ? "won" : "lost")); // TODO: get Vault currency instead of $ and check to make sure vault is enabled
        }
        broadcastMessage(GREEN, "The " + team.getChatColor() + team.getTitleName() + GREEN + " has won!", "The " + team.getTitleName() + GREEN + " has won!");
        broadcastMessage(GREEN, ChatColor.STRIKETHROUGH + Settings.THEME + "                              ", "");
        new GameFinishCountdown(WIN_WAIT_TIME, this);
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
        for (Team team : getArenaTeamList()) {
            // teams.remove(team, teams.get(team));
            // teams.put(team, 0);
            teams.replace(team, teams.get(team), 0);
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
            case STARTING:
                color = RED;
                break;
            case NOT_SETUP:
                color = GRAY;
                break;
        }
        return color + state.toString();
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
    public void advSave() {
        ARENA.saveFile();
        /**
         * Because the saveArenaFile() method gets called every time a value is changed,
         * we also want to see if the arena is setup because if it is, Arena.NOT_SETUP should
         * be replaced with ArenaState.WAITING (or ArenaState.DISABLED) because the setup is complete.
         */
        if (isSetup() && isEnabled()) {
            state = ArenaState.WAITING;
        } else if (isSetup() && !isEnabled()) {
            state = ArenaState.DISABLED;
        }
    }

    int loaded = 0;
    // TODO: put this stuff in the arenafile class
    private int loadInt(String item) {
        FileConfiguration config = Settings.getSettings().getConfig();
        if (config.getString(getConfigPath(item)) != null && config.getString(getConfigPath(item)).equalsIgnoreCase("default")) {
            return config.getInt(getDefaultConfigPath(item));
        } else {
            loaded++;
            System.out.println("Loading int " + item + ". Value: " + config.getInt(getConfigPath(item)));
            return config.getInt(getConfigPath(item));
        }
    }

    private String loadString(String item) {
        FileConfiguration config = Settings.getSettings().getConfig();
        if (config.getString(getConfigPath(item)) != null && config.getString(getConfigPath(item)).equalsIgnoreCase("default")) {
            return ChatColor.translateAlternateColorCodes('&', config.getString(getDefaultConfigPath(item)));
        } else {
            loaded++;
            System.out.println("Loading string " + item + ". Value: " + config.getString(getConfigPath(item)) + loaded);
            return ChatColor.translateAlternateColorCodes('&', config.getString(getConfigPath(item)));
        }
    }

    private boolean loadBoolean(String item) {
        FileConfiguration config = Settings.getSettings().getConfig();
        if (config.getString(getConfigPath(item)) != null && config.getString(getConfigPath(item)).equalsIgnoreCase("default")) {
            return config.getBoolean(getDefaultConfigPath(item));
        } else {
            loaded++;
            System.out.println("Loading boolean " + item + ". Value: " + config.getBoolean(getConfigPath(item)));
            return config.getBoolean(getConfigPath(item));
        }
    }

    public void loadConfigValues() {
        MAX_SCORE                   = loadInt("max-score");
        TIME                        = loadInt("time");
        WIN_WAIT_TIME               = loadInt("win-waiting-time");
        ARENA_COUNTDOWN             = loadInt("Countdown.arena.countdown");
        ARENA_INTERVAL              = loadInt("Countdown.arena.interval");
        ARENA_NO_INTERVAL           = loadInt("Countdown.arena.no-interval");
        LOBBY_COUNTDOWN             = loadInt("Countdown.lobby.countdown");
        LOBBY_INTERVAL              = loadInt("Countdown.lobby.interval");
        LOBBY_NO_INTERVAL           = loadInt("Countdown.lobby.no-interval");
        KILLCOIN_PER_KILL           = loadInt("Rewards.Kill-Coins.per-kill");
        KILLCOIN_PER_DEATH          = loadInt("Rewards.Kill-Coins.per-death");
        MONEY_PER_KILL              = loadInt("Rewards.Money.per-kill");
        MONEY_PER_DEATH             = loadInt("Rewards.Money.per-death");
        MONEY_PER_WIN               = loadInt("Rewards.Money.per-win");
        MONEY_PER_DEFEAT            = loadInt("Rewards.Money.per-defeat");
        SAFE_TIME                   = loadInt("safe-time");

        BROADCAST_WINNER           = loadBoolean("Chat.broadcast-winner");
        PER_TEAM_CHAT_LOBBY        = loadBoolean("Join-Lobby.per-team-chat");
        PER_TEAM_CHAT_ARENA        = loadBoolean("Join-Arena.per-team-chat");
        KILL_COIN_SHOP             = loadBoolean("kill-coin-shop");
        GIVE_WOOL_HELMET_ARENA     = loadBoolean("Join-Arena.give-wool-helmet");
        GIVE_WOOL_HELMET_LOBBY     = loadBoolean("Join-Lobby.give-wool-helmet");
        COLOR_PLAYER_TITLE_LOBBY   = loadBoolean("Join-Lobby.color-player-title");
        COLOR_PLAYER_TITLE_ARENA   = loadBoolean("Join-Arena.color-player-title");
        GIVE_TEAM_SWITCHER         = loadBoolean("Join-Lobby.give-team-switcher");
        USE_ARENA_CHAT             = loadBoolean("Chat.arena-chat");

        ARENA_CHAT                 = loadString("Chat.arena-chat");
        SPEC_CHAT                  = loadString("Chat.spectator-chat");
    }
    // TODO: add events so players can't teleport and stuff inside arena
}