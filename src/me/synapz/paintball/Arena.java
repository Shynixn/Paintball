package me.synapz.paintball;


import com.connorlinfoot.titleapi.TitleAPI;
import com.google.common.base.Joiner;
import me.synapz.paintball.countdowns.ArenaStartCountdown;
import me.synapz.paintball.countdowns.GameCountdown;
import me.synapz.paintball.countdowns.GameFinishCountdown;
import me.synapz.paintball.countdowns.LobbyCountdown;
import me.synapz.paintball.locations.SignLocation;
import me.synapz.paintball.locations.SpectatorLocation;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.players.*;
import me.synapz.paintball.storage.Settings;

import static me.synapz.paintball.locations.TeamLocation.*;
import static me.synapz.paintball.storage.Settings.*;
import static org.bukkit.ChatColor.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
    public int HITS_TO_KILL;
    public int LIVES;
    public int TEAM_SWITCH_COOLDOWN;

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
    public boolean DISABLE_ALL_COMMANDS;
    public boolean ALL_PAINTBALL_COMMANDS;
    public boolean KILL_COINS_NEGATIVE;
    public boolean MONEY_NEGATIVE;

    public List<String> BLOCKED_COMMANDS;
    public List<String> ALLOWED_COMMANDS;

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

    private Map<Location, SignLocation> signLocations = new HashMap<>();

    private ArenaState state;

    public enum ArenaState {
        NOT_SETUP,
        WAITING,
        DISABLED,
        STARTING,
        STOPPING,
        IN_PROGRESS;

        @Override
        public String toString() {
            // This giant line replaces _ with a space, and makes the first letter capital
            return super.toString().toLowerCase().replace("_", " ").replaceFirst(String.valueOf(super.toString().toLowerCase().toCharArray()[0]), String.valueOf(super.toString().toUpperCase().toCharArray()[0]));
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
        setState(ArenaState.NOT_SETUP);

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
    public Location getLocation(TeamLocations type, Team team, int spawnNumber) {
        return new TeamLocation(this, team, type, spawnNumber).getLocation();
    }

    // Sets the spawn of a team in the arena to a location
    public void setLocation(TeamLocations type, Location location, Team team) {
        new TeamLocation(this, team, location, type);
    }

    public void delLocation(TeamLocations type, Team team, int spawnNumber) {
        new TeamLocation(this, team, type, spawnNumber).removeLocation();
    }

    public Location getSpectatorLocation() {
        return new SpectatorLocation(this, Utils.randomNumber(ARENA_FILE.getConfigurationSection(this.getPath() + "Spectator") == null ? 1 : ARENA_FILE.getConfigurationSection(this.getPath() + "Spectator").getValues(false).size())).getLocation();
    }

    public void removeSpectatorLocation() {
        new SpectatorLocation(this, ARENA_FILE.getConfigurationSection(this.getPath() + "Spectator").getValues(false).size()).removeLocation();
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
            teamColors.add(t.getChatColor() + ":" + t.getTitleName());
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
            steps.add(ARENA_FILE.getString(t.getPath(TeamLocations.LOBBY, 1)) != null ? done + lobbyName + end : lobbyName);
            steps.add(ARENA_FILE.getString(t.getPath(TeamLocations.SPAWN, 1)) != null ? done + spawnName + end : spawnName);
        }
        Utils.addItemsToArray(steps, (ARENA_FILE.getString(this.getPath() + "Spectator.1") != null ? done + "setspec" + end : "setspec"), isEnabled() ? done + "enable" + end : "enable", getArenaTeamList().isEmpty() ? "setteams" : "");
        finalString = GRAY + Joiner.on(", ").join(steps);

        return isSetup() && isEnabled() ? prefix + GRAY + "Complete. Arena is open!" : prefix + finalString;
    }

    // Set the arena to be enabled/disabled
    public void setEnabled(boolean setEnabled) {
        if (setEnabled) {
            setState(ArenaState.WAITING);
        } else {
            broadcastMessage(toString() + RED + " has been disabled.");
            for (PaintballPlayer player : getAllPlayers().values())
                player.forceLeaveArena();
            setState(ArenaState.DISABLED);
        }
        ARENA_FILE.set(getPath() + "Enabled", setEnabled);
        advSave();
    }

    // Checks weather this arena is setup or not. In order to be setup max, min, spectator and all spawns must be set
    public boolean isSetup() {
        boolean spawnsSet = true;
        boolean isSpectateSet = (ARENA_FILE.getString(this.getPath() + "Spectator.1") != null);
        if (getArenaTeamList().isEmpty()) {
            spawnsSet = false;
        }
        for (Team t : getArenaTeamList()) {
            if (ARENA_FILE.getString(t.getPath(TeamLocations.SPAWN, 1)) == null) {
                spawnsSet = false;
            }
            // TODO make t.getPath(type) instead of boolean
            if (ARENA_FILE.getString(t.getPath(TeamLocations.LOBBY, 1))== null) {
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
            setState(ArenaState.WAITING);
        } else {
            if (!isEnabled() && isSetup()) {
                setState(ArenaState.DISABLED);
            }
        }
    }

    public void updateSigns() {
        String prefix = DARK_GRAY + "[" + THEME + "Paintball" + DARK_GRAY + "]";
        for (SignLocation signLoc : getSignLocations().values()) {
            Location loc = signLoc.getLocation();
            if (loc != null && loc.getBlock() != null && loc.getBlock().getState() != null && !(loc.getBlock().getState() instanceof Sign)) {
                signLoc.removeSign();
                return;
            }
            Sign sign = (Sign) loc.getBlock().getState();
            int counter = Utils.getCurrentCounter(this);
            sign.setLine(0, prefix); // in case the prefix changes
            sign.setLine(1, getName()); // in case they rename it
            sign.setLine(2, getStateAsString() + " " + (counter == 0 ? "" : counter + "s")); // TODO: put times here ;)
            sign.setLine(3, getMax() <= 0 ? "0/0" : getLobbyPlayers().size() == getMax() || getAllArenaPlayers().size() == getMax() ? RED + "Full" : (state == Arena.ArenaState.WAITING ? getLobbyPlayers().size() + "" : state == ArenaState.IN_PROGRESS || state == ArenaState.STARTING ? getAllArenaPlayers().size() + "" : "0") + "/" + getMax());
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
            if (LobbyCountdown.tasks.get(this) != null)
                LobbyCountdown.tasks.get(this).cancel();
            if (ArenaStartCountdown.tasks.get(this) != null)
                ArenaStartCountdown.tasks.get(this).cancel();
            startGame();
        } else {
            setState(ArenaState.WAITING);
            this.broadcastMessage(this.toString() + RED + " has been force stopped.");
            this.forceLeaveArena();
        }
    }

    // Put the arena to string ex: Arena Syn, where Syn is the arena name. At the end it is green so if you don't want it to be green change it after
    // TODO: chat color support
    public String toString() {
        return "Arena " + GRAY + this.getName() + GREEN;
    }
    
    // Return the path the Arena is at with a . at the end (This is where defaultName comes in handy)
    public String getPath() {
        return "Arenas." + defaultName + ".";
    }

    public void joinLobby(Player player, Team team) {
        if (Utils.canJoin(player, this))
            new LobbyPlayer(this, team == null ? getTeamWithLessPlayers() : team, player);
    }

    // Starts the game, turns all LobbyPlayers into ArenaPlayers
    public void startGame() {
        HashMap<Player, Location> startLocs = new HashMap<>();
        setState(ArenaState.STARTING);
        for (LobbyPlayer p : lobby) {
            allPlayers.remove(p.getPlayer(), p);
            ArenaPlayer player = new ArenaPlayer(this, p.getTeam(), p.getPlayer());
            startLocs.put(player.getPlayer(), player.getPlayer().getLocation());
        }
        lobby.removeAll(lobby);
        new ArenaStartCountdown(ARENA_COUNTDOWN, this, startLocs);
    }
    
    // Used for server reload and arena force stops, so no messages will be sent
    public void stopGame() {
        setState(ArenaState.WAITING);
        this.forceLeaveArena();
    }

    public void forceLeaveArena() {
        List<PaintballPlayer> copiedList = new ArrayList<>(allPlayers.values());
        for (PaintballPlayer player : copiedList)
            player.forceLeaveArena();
        allPlayers = new HashMap<>();
        lobby = new ArrayList<>();
        spectators = new ArrayList<>();
        inGame = new ArrayList<>();
        updateSigns();
        this.resetTeamScores();
    }

    // Called when a team wins
    public void win(List<Team> teams) {
        if (BROADCAST_WINNER) {
            // TODO: broadcast to server / network (except for those in game, they get a different message)
        }

        for (ArenaPlayer arenaPlayer : getAllArenaPlayers()) {
            Player player = arenaPlayer.getPlayer();
            if (teams.contains(arenaPlayer.getTeam()))
                arenaPlayer.setWon();
            String spaces = Settings.SECONDARY + ChatColor.STRIKETHROUGH + Utils.makeSpaces(20);
            String title = THEME + " Games Stats ";
            Messenger.msg(player, spaces + title + spaces,
                    (arenaPlayer.getMoney() < 0 ? "-" : "") + "$" + Math.abs(arenaPlayer.getMoney()),
                    "Kills: " + arenaPlayer.getKills(),
                    "Deaths: " + arenaPlayer.getDeaths(),
                    "Killstreak: " + arenaPlayer.getKillStreak(),
                    "KD: " + arenaPlayer.getKd(),
                    "Your team " + (teams.contains(arenaPlayer.getTeam()) ? "won" : "lost"),
                    spaces + Utils.makeSpaces(title +  "123") + spaces);
        }

        StringBuilder formattedWinnerList = new StringBuilder();
        for (Team winningTeam : teams) {
            formattedWinnerList.append(winningTeam.getChatColor()).append(winningTeam.getTitleName()).append(Settings.THEME).append(", ");
        }
        String list = formattedWinnerList.substring(0, formattedWinnerList.lastIndexOf(", "));
        broadcastMessage((teams.size() == 1 ? "The " + list + " team won!": "There was a tie between " + formattedWinnerList.toString()));
        for (PaintballPlayer player : getAllPlayers().values())
            TitleAPI.sendTitle(player.getPlayer(), 20, 40, 20, THEME +(teams.size() == 1 ? "The " + list + " won" : "There was a tie between"), SECONDARY + (teams.size() == 1 ? "You " + (teams.contains(player.getTeam()) ? "won" : "lost"): formattedWinnerList.toString()));
        new GameFinishCountdown(WIN_WAIT_TIME, this);
    }

    // Broadcasts a message to the whole arena
    public void broadcastMessage(String... chatMessage) {
        for (Player player : allPlayers.keySet()) {
            for (String msg : chatMessage)
                player.sendMessage(PREFIX + msg);
        }
    }

    public void broadcastTitle(String header, String footer, int fadeIn, int stay, int fadeOut) {
        if (!TITLE_API)
            return;

        for (Player player : allPlayers.keySet()) {
            TitleAPI.sendTitle(player, fadeIn, stay, fadeOut, header, footer);
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
            setState(ArenaState.NOT_SETUP);
        }
        return state;
    }

    // Set the arena's state
    public void setState(ArenaState state) {
        this.state = state;
        // Since state is changing, update arena's signs
        this.updateSigns();
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
                color = DARK_RED;
                break;
            case STOPPING:
                color = DARK_RED;
                break;
            case STARTING:
                color = DARK_RED;
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

    public void updateAllScoreboard() {
        for (PaintballPlayer player : getAllPlayers().values()) {
            if (player instanceof ScoreboardPlayer)
                ((ScoreboardPlayer) player).updateScoreboard();
        }
    }

    public void updateAllScoreboardTimes() {
        for (PaintballPlayer player : getAllPlayers().values()) {
            if (player instanceof ScoreboardPlayer)
                ((ScoreboardPlayer) player).updateDisplayName();
        }
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
            setState(ArenaState.WAITING);
        } else if (isSetup() && !isEnabled()) {
            setState(ArenaState.DISABLED);
        } else if (!isSetup()) {
            setState(ArenaState.NOT_SETUP);
        }
    }

    public void loadConfigValues() {
        FileConfiguration config = Settings.getSettings().getConfig();

        MAX_SCORE                   = ARENA.loadInt("max-score", this);
        TIME                        = ARENA.loadInt("time", this);
        WIN_WAIT_TIME               = ARENA.loadInt("win-waiting-time", this);
        ARENA_COUNTDOWN             = ARENA.loadInt("Countdown.arena.countdown", this);
        ARENA_INTERVAL              = ARENA.loadInt("Countdown.arena.interval", this);
        ARENA_NO_INTERVAL           = ARENA.loadInt("Countdown.arena.no-interval", this);
        LOBBY_COUNTDOWN             = ARENA.loadInt("Countdown.lobby.countdown", this);
        LOBBY_INTERVAL              = ARENA.loadInt("Countdown.lobby.interval", this);
        LOBBY_NO_INTERVAL           = ARENA.loadInt("Countdown.lobby.no-interval", this);
        KILLCOIN_PER_KILL           = ARENA.loadInt("Rewards.Kill-Coins.per-kill", this);
        KILLCOIN_PER_DEATH          = ARENA.loadInt("Rewards.Kill-Coins.per-death", this);
        MONEY_PER_KILL              = ARENA.loadInt("Rewards.Money.per-kill", this);
        MONEY_PER_DEATH             = ARENA.loadInt("Rewards.Money.per-death", this);
        MONEY_PER_WIN               = ARENA.loadInt("Rewards.Money.per-win", this);
        MONEY_PER_DEFEAT            = ARENA.loadInt("Rewards.Money.per-defeat", this);
        SAFE_TIME                   = ARENA.loadInt("safe-time", this);
        HITS_TO_KILL                = ARENA.loadInt("hits-to-kill", this);
        LIVES                       = ARENA.loadInt("lives", this);
        TEAM_SWITCH_COOLDOWN        = ARENA.loadInt("team-switch-cooldown", this);

        PER_TEAM_CHAT_LOBBY        = ARENA.loadBoolean("Join-Lobby.per-team-chat", this);
        PER_TEAM_CHAT_ARENA        = ARENA.loadBoolean("Join-Arena.per-team-chat", this);
        KILL_COIN_SHOP             = ARENA.loadBoolean("kill-coin-shop", this);
        GIVE_WOOL_HELMET_ARENA     = ARENA.loadBoolean("Join-Arena.give-wool-helmet", this);
        GIVE_WOOL_HELMET_LOBBY     = ARENA.loadBoolean("Join-Lobby.give-wool-helmet", this);
        COLOR_PLAYER_TITLE_LOBBY   = ARENA.loadBoolean("Join-Lobby.color-player-title", this);
        COLOR_PLAYER_TITLE_ARENA   = ARENA.loadBoolean("Join-Arena.color-player-title", this);
        GIVE_TEAM_SWITCHER         = ARENA.loadBoolean("Join-Lobby.give-team-switcher", this);
        USE_ARENA_CHAT             = ARENA.loadBoolean("Chat.use-arena-chat", this);
        BROADCAST_WINNER           = ARENA.loadBoolean("Chat.broadcast-winner", this);
        KILL_COINS_NEGATIVE        = ARENA.loadBoolean("Rewards.Kill-Coins.can-be-negative", this);
        MONEY_NEGATIVE             = ARENA.loadBoolean("Rewards.Money.can-be-negative", this);

        DISABLE_ALL_COMMANDS       = config.getBoolean("Commands.disable-all-commands");
        ALL_PAINTBALL_COMMANDS     = config.getBoolean("Commands.all-paintball-commands");

        ARENA_CHAT                 = ARENA.loadString("Chat.arena-chat", this);
        SPEC_CHAT                  = ARENA.loadString("Chat.spectator-chat", this);

        BLOCKED_COMMANDS           = config.getStringList("Commands.Blocked");
        ALLOWED_COMMANDS           = config.getStringList("Commands.Allowed");
    }
}