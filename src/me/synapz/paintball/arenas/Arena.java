package me.synapz.paintball.arenas;


import com.google.common.base.Joiner;
import me.synapz.paintball.countdowns.ArenaStartCountdown;
import me.synapz.paintball.countdowns.GameFinishCountdown;
import me.synapz.paintball.countdowns.LobbyCountdown;
import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.locations.SignLocation;
import me.synapz.paintball.locations.SpectatorLocation;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.players.*;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Title;
import me.synapz.paintball.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.*;

import static me.synapz.paintball.locations.TeamLocation.TeamLocations;
import static me.synapz.paintball.storage.Settings.*;
import static org.bukkit.ChatColor.*;

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
    public int COIN_PER_KILL;
    public int COIN_PER_DEATH;
    public int MONEY_PER_KILL;
    public int MONEY_PER_DEATH;
    public int MONEY_PER_WIN;
    public int MONEY_PER_DEFEAT;
    public int SAFE_TIME;
    public int HITS_TO_KILL;
    public int LIVES;
    public int TEAM_SWITCH_COOLDOWN;
    public int SPEED;
    public int RESPAWN_TIME;

    public String ARENA_CHAT;
    public String SPEC_CHAT;
    public String CURRENCY;
    public Material COIN_SHOP_TYPE;

    public boolean COINS;
    public boolean FIREWORK_ON_DEATH;
    public boolean STOP_PROT_ON_HIT;
    public boolean ACCURACY;
    public boolean BROADCAST_WINNER;
    public boolean PER_TEAM_CHAT_LOBBY;
    public boolean PER_TEAM_CHAT_ARENA;
    public boolean COIN_SHOP;
    public boolean GIVE_TEAM_SWITCHER;
    public boolean USE_ARENA_CHAT;
    public boolean DISABLE_ALL_COMMANDS;
    public boolean ALL_PAINTBALL_COMMANDS;
    public boolean TELEPORT_TEAM_SWITCH;
    public boolean ARENA_WOOL_HELMET;
    public boolean LOBBY_WOOL_HELMET;
    public boolean NAMETAGS;
    public boolean PER_TEAM_CHAT;

    public List<String> BLOCKED_COMMANDS;
    public List<String> ALLOWED_COMMANDS;

    // All the players in the arena (Lobby, Spec, InGame) linked to the player which is linked to the PaintballPLayer
    private Map<Player, PaintballPlayer> allPlayers = new HashMap<>();
    private List<SpectatorPlayer> spectators = new ArrayList<>();
    private List<LobbyPlayer> lobby = new ArrayList<>();
    private List<ArenaPlayer> inGame = new ArrayList<>();

    // Arena name is the current name of the arena
    // Arena currentName is the name set when setup, this is used for renaming: you can't change a path name so we keep the currentName for accessing paths
    private String defaultName, currentName;
    // Team with their size
    private Map<Team, Integer> teams = new HashMap<>();

    private Map<Location, SignLocation> signLocations = new HashMap<>();

    private ArenaState state;
    private boolean toReload;

    public enum ArenaState {
        NOT_SETUP,
        WAITING,
        DISABLED,
        STARTING,
        STOPPING,
        IN_PROGRESS,
        REMOVED;

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

    public ArenaType getArenaType() {
        return ArenaType.TDM;
    }

    public void setArenaType(ArenaType type) {
        ARENA_FILE.set(getPath() + "Type", type.getStaticName());
        Settings.ARENA.loadArenasFromFile();
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
        for (PaintballPlayer player : getAllPlayers().values()) {
            player.leave();
            Messenger.error(player.getPlayer(), Messages.ARENA_REMOVE);
        }

        setState(ArenaState.REMOVED);
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

    public boolean isMaxSet() {
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
    public String getSteps() {
        List<String> steps = new ArrayList<String>();
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
            List<PaintballPlayer> copyFile = new ArrayList<>(getAllPlayers().values());
            broadcastMessage(this.toString(RED) + " has been disabled.");
            for (PaintballPlayer player : copyFile)
                player.leave();
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
            sign.setLine(2, getStateAsString() + " " + (counter == -1 ? "" : counter + "s"));
            sign.setLine(3, getMax() <= 0 ? "0/0" : getLobbyPlayers().size() == getMax() || getAllArenaPlayers().size() == getMax() ? RED + "Full" : (state == Arena.ArenaState.WAITING ? getLobbyPlayers().size() + "" : state == ArenaState.IN_PROGRESS || state == ArenaState.STARTING ? getAllArenaPlayers().size() + "" : "0") + "/" + getMax());
            sign.update();
        }
    }

    public String getSign() {
        int counter = Utils.getCurrentCounter(this);
        String sign = getName() + "," + getStateAsString() + " " + (counter == -1 ? "" : counter + "s") + "," +
                (getMax() <= 0 ? "0/0" : getLobbyPlayers().size() == getMax() || getAllArenaPlayers().size() ==
                        getMax() ? RED + "Full" : (state == Arena.ArenaState.WAITING ? getLobbyPlayers().size() + "" :
                        state == ArenaState.IN_PROGRESS || state == ArenaState.STARTING ? getAllArenaPlayers().size() + ""
                                : "0") + "/" + getMax());
        return sign;
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
            this.broadcastMessage(new MessageBuilder(Messages.ARENA_FORCE_STOPPED).replace(Tag.ARENA, this.toString(RED)).build());
            this.forceLeaveArena();
        }
    }

    // Put the arena to string ex: Arena Syn, where Syn is the arena name. At the end it is green so if you don't want it to be green change it after
    public String toString(ChatColor color) {
        return color + "Arena " + GRAY + this.getName() + color;
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
            ArenaPlayer player;

            if (this instanceof CTFArena)
                player = new CTFArenaPlayer(p);
            else if (this instanceof RTFArena)
                player = new RTFArenaPlayer(p);
            else if (this instanceof FFAArena)
                player = new FFAArenaPlayer(p);
            else if (this instanceof DOMArena)
                player = new DOMArenaPlayer(p);
            else if (this instanceof LTSArena)
                player = new LTSArenaPlayer(p);
            else if (this instanceof DTCArena)
                player = new DTCArenaPlayer(p);
            else if (this instanceof SFGArena)
                player = new SFGArenaPlayer(p);
            else if (this instanceof KCArena)
                player = new KCArenaPlayer(p);
            else
                player = new ArenaPlayer(p);

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
            player.leave();

        // Update the save stats
        ArenaManager.getArenaManager().updateAllSignsOnServer();

        allPlayers = new HashMap<>();
        lobby = new ArrayList<>();
        spectators = new ArrayList<>();
        inGame = new ArrayList<>();
        setState(ArenaState.WAITING);
        this.resetTeamScores();

        if (toReload) {
            this.toReload = false;
            this.loadConfigValues();
        }
    }

    public void setReload(){
        this.toReload = true;
    }

    // Called when a team wins
    public void win(List<Team> teams) {
        for (ArenaPlayer arenaPlayer : getAllArenaPlayers()) {
            Player player = arenaPlayer.getPlayer();
            if (teams.contains(arenaPlayer.getTeam())) {
                if (teams.size() != 1) {
                    arenaPlayer.setTie();
                } else {
                    arenaPlayer.setWon();
                }

                final Firework firework = player.getWorld().spawn(player.getLocation().add(0, 4, 0), Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffects(FireworkEffect.builder().withColor(arenaPlayer.getTeam().getColor(), arenaPlayer.getTeam().getColor()).withTrail().build());
                firework.setVelocity(firework.getVelocity().multiply(10));
                firework.setFireworkMeta(meta);
            }

            String spaces = Settings.SECONDARY + ChatColor.STRIKETHROUGH + Utils.makeSpaces(20);
            String title = THEME + " Games Stats ";
            Messenger.msg(player, spaces + title + spaces,
                    Settings.THEME + "Money: " + Settings.SECONDARY + (arenaPlayer.getMoney() < 0 ? "-" : "+") + "$" + Math.abs(arenaPlayer.getMoney()),
                    Settings.THEME + "Kills: " + Settings.SECONDARY + arenaPlayer.getKills(),
                    Settings.THEME + "Deaths: " + Settings.SECONDARY + arenaPlayer.getDeaths(),
                    Settings.THEME + "Killstreak: " + Settings.SECONDARY + arenaPlayer.getKillStreak(),
                    Settings.THEME + "KD: " + Settings.SECONDARY + arenaPlayer.getKd(),
                    "Your team " + (teams.size() >= 2 ? "tied" : (teams.contains(arenaPlayer.getTeam()) ? "won" : "lost")),
                    spaces + Utils.makeSpaces(title +  "123") + spaces);

            if (arenaPlayer.isWinner()) {
                // TODO implement a glow api with reflection.
            }
        }

        StringBuilder formattedWinnerList = new StringBuilder();
        for (Team winningTeam : teams) {
            formattedWinnerList.append(winningTeam.getChatColor()).append(winningTeam.getTitleName()).append(Settings.THEME).append(", ");
        }

        String list = formattedWinnerList.substring(0, formattedWinnerList.lastIndexOf(", "));

        if (BROADCAST_WINNER) {
            Bukkit.broadcastMessage((teams.size() == 1 ? "The " + list + " team won!" : "There was a tie between " + list));
        } else {
            broadcastMessage((teams.size() == 1 ? "The " + list + " team won!" : "There was a tie between " + list));
        }

        for (PaintballPlayer player : getAllPlayers().values()) {
            Title title = new Title(THEME + (teams.size() == 1 ? "The " + list + " won" : "There was a tie between"), SECONDARY + (teams.size() == 1 ? "You " + (teams.contains(player.getTeam()) ? "won" : "lost") : list), 20, 40, 20);
            title.send(player.getPlayer());
        }
        new GameFinishCountdown(WIN_WAIT_TIME, this);
    }

    // Broadcasts a message to the whole arena
    public void broadcastMessage(String chatMessage) {
        for (Player player : allPlayers.keySet())
                Messenger.info(player, chatMessage);
    }

    public void broadcastMessage(Messages chatMessage) {
        for (Player player : allPlayers.keySet())
            Messenger.info(player, chatMessage);
    }

    public void broadcastTitle(String header, String footer, int fadeIn, int stay, int fadeOut) {
        for (Player player : allPlayers.keySet()) {
            Title title = new Title(header, footer, fadeIn, stay, fadeOut);
            title.send(player);
        }
    }

    public void broadcastTitle(Messages header, Messages footer, int fadeIn, int stay, int fadeOut) {
        for (Player player : allPlayers.keySet()) {
            Title title = new Title(header.getString(), footer.getString(), fadeIn, stay, fadeOut);
            title.send(player);
        }
    }


    // Returns the team with less players for when someone joins
    protected Team getTeamWithLessPlayers() {
        return Utils.max(this);
    }

    // If we can start the lobby timer
    public boolean canStartTimer() {
        int size = lobby.size();
        return size >= this.getMin() && size <= this.getMax();
    }

    public void resetTeamScores() {
        for (Team team : getArenaTeamList()) {
            teams.replace(team, teams.get(team), 0);
        }
    }

    public int getTeamScore(Team team) {
        return teams.get(team);
    }

    public void incrementTeamScore(Team team, boolean doWinCheck) {
        teams.replace(team, teams.get(team), teams.get(team)+1);

        if (doWinCheck && teams.get(team) == MAX_SCORE)
            win(Arrays.asList(team));
    }

    public void decrementTeamScore(Team team) {
        teams.replace(team, teams.get(team), teams.get(team)-1);
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
            case REMOVED:
                color = DARK_RED;
                break;
        }
        return color + state.toString();
    }

    public void removePlayer(PaintballPlayer pbPlayer, boolean restoreData) {
        if (restoreData)
            Settings.PLAYERDATA.restorePlayerInformation(pbPlayer.getPlayer());
        allPlayers.remove(pbPlayer.getPlayer(), pbPlayer);
        lobby.remove(pbPlayer);
        inGame.remove(pbPlayer);
        spectators.remove(pbPlayer);
        updateSigns();
    }

    public void addPlayer(PaintballPlayer pbPlayer) {
        if (!allPlayers.values().contains(pbPlayer)) {
            allPlayers.put(pbPlayer.getPlayer(), pbPlayer);
        }

        if (pbPlayer instanceof LobbyPlayer && !lobby.contains(pbPlayer)) {
            lobby.add((LobbyPlayer) pbPlayer);
        } else if (pbPlayer instanceof ArenaPlayer && !inGame.contains(pbPlayer)) {
            inGame.add((ArenaPlayer) pbPlayer);
        } else if (pbPlayer instanceof SpectatorPlayer && !spectators.contains(pbPlayer)) {
            spectators.add((SpectatorPlayer) pbPlayer);
        }
        updateSigns();
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
        return new HashMap<>(allPlayers);
    }

    public void updateAllScoreboard() {
        for (PaintballPlayer player : getAllPlayers().values()) {
            if (player instanceof ScoreboardPlayer)
                ((ScoreboardPlayer) player).updateScoreboard();
        }
    }

    public void updateAllScoreboardTimes() {
        for (PaintballPlayer player : getAllPlayers().values()) {
                player.updateDisplayName();
        }
    }

    public int getTeamMax(Team team) {
        // 8 % 3 = 3
        int extra = (int) Math.round((double) this.getMax()%this.getArenaTeamList().size());
        // 8 / 3 = 2
        int maxPer = (int) Math.round((double) this.getMax()/this.getArenaTeamList().size());

        if (getArenaTeamList().size() % 2 != 2 && extra != 0)
            extra = (int) (double) this.getMax()%this.getArenaTeamList().size()-1;

        // Put each one in
        Map<Team, Integer> perMax = new HashMap<Team, Integer>() {{
            for (Team t : getArenaTeamList()) {
                put(t, maxPer);
            }
        }};

        while (extra != 0) {
            for (Team t : getArenaTeamList()) {
                if (extra == 0) {
                    break;
                } else {
                    int oldMax = perMax.get(t);
                    int newMax = --oldMax;
                    perMax.remove(t, oldMax);
                    perMax.put(t, newMax);
                    extra--;
                }
            }
        }

        return perMax.get(team);
    }

    // Saves arena file along with other checks
    public void advSave() {
        ARENA.saveFile();
        /**
         * Because the saveArenaFile() method gets called every time a value is changed,
         * we also want to see if the arena is setup because if it is, Arena.NOT_SETUP should
         * be replaced with ArenaState.WAITING (or ArenaState.DISABLED) because the setup is complete.
         */

        if (state == ArenaState.REMOVED) {
            return;
        } else if (isSetup() && isEnabled()) {
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
        SAFE_TIME                   = ARENA.loadInt("safe-time", this);
        HITS_TO_KILL                = ARENA.loadInt("hits-to-kill", this);
        LIVES                       = ARENA.loadInt("lives", this);
        TEAM_SWITCH_COOLDOWN        = ARENA.loadInt("team-switch-cooldown", this);
        SPEED                       = ARENA.loadInt("speed", this);
        CURRENCY                    = ARENA.loadString("currency", this);
        RESPAWN_TIME                = ARENA.loadInt("respawn-time", this);

        ARENA_COUNTDOWN             = ARENA.loadInt("Countdown.arena.countdown", this);
        ARENA_INTERVAL              = ARENA.loadInt("Countdown.arena.interval", this);
        ARENA_NO_INTERVAL           = ARENA.loadInt("Countdown.arena.no-interval", this);
        LOBBY_COUNTDOWN             = ARENA.loadInt("Countdown.lobby.countdown", this);
        LOBBY_INTERVAL              = ARENA.loadInt("Countdown.lobby.interval", this);
        LOBBY_NO_INTERVAL           = ARENA.loadInt("Countdown.lobby.no-interval", this);

        COIN_PER_KILL               = ARENA.loadInt("Rewards.Coins.per-kill", this);
        COIN_PER_DEATH              = ARENA.loadInt("Rewards.Coins.per-death", this);
        MONEY_PER_KILL              = ARENA.loadInt("Rewards.Money.per-kill", this);
        MONEY_PER_DEATH             = ARENA.loadInt("Rewards.Money.per-death", this);
        MONEY_PER_WIN               = ARENA.loadInt("Rewards.Money.per-win", this);
        MONEY_PER_DEFEAT            = ARENA.loadInt("Rewards.Money.per-defeat", this);

        COINS                      = ARENA.loadBoolean("coins", this);
        FIREWORK_ON_DEATH          = ARENA.loadBoolean("firework-on-death", this);
        STOP_PROT_ON_HIT           = ARENA.loadBoolean("cancel-prot-on-hit", this);
        ACCURACY                   = ARENA.loadBoolean("accuracy", this);
        PER_TEAM_CHAT_LOBBY        = ARENA.loadBoolean("Join-Lobby.per-team-chat", this);
        PER_TEAM_CHAT_ARENA        = ARENA.loadBoolean("Join-Arena.per-team-chat", this);
        COIN_SHOP                  = ARENA.loadBoolean("coin-shop", this);
        GIVE_TEAM_SWITCHER         = ARENA.loadBoolean("Join-Lobby.give-team-switcher", this);
        USE_ARENA_CHAT             = ARENA.loadBoolean("Chat.use-arena-chat", this);
        BROADCAST_WINNER           = ARENA.loadBoolean("Chat.broadcast-winner", this);
        TELEPORT_TEAM_SWITCH       = ARENA.loadBoolean("teleport-on-team-switch", this);
        ARENA_WOOL_HELMET          = ARENA.loadBoolean("Join-Arena.wool-helmet", this);
        LOBBY_WOOL_HELMET          = ARENA.loadBoolean("Join-Lobby.wool-helmet", this);
        NAMETAGS                   = ARENA.loadBoolean("nametags", this);

        DISABLE_ALL_COMMANDS       = config.getBoolean("Commands.disable-all-commands");
        ALL_PAINTBALL_COMMANDS     = config.getBoolean("Commands.all-paintball-commands");

        ARENA_CHAT                 = ARENA.loadString("Chat.arena-chat", this);
        SPEC_CHAT                  = ARENA.loadString("Chat.spectator-chat", this);

        BLOCKED_COMMANDS           = config.getStringList("Commands.Blocked");
        ALLOWED_COMMANDS           = config.getStringList("Commands.Allowed");

        COIN_SHOP_TYPE             = Utils.loadMaterial(ARENA.loadString("coin-shop-type", this), Material.MAGMA_CREAM);
    }
}