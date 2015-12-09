package me.synapz.paintball;


import com.connorlinfoot.titleapi.TitleAPI;
import com.google.common.base.Joiner;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.LobbyPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.players.SpectatorPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.ChatColor.*;

public class Arena {

    String maxPath = "Max-Players";
    String minPath = "Min-Players";
    String enabledPath = "Is-Enabled";
    String spectatePath = "Spectate-Loc";
    ArenaState state = ArenaState.NOT_SETUP;
    private FileConfiguration FILE = Settings.getSettings().getArenaFile();
    private Map<Player, PaintballPlayer> allPlayers = new HashMap<Player, PaintballPlayer>();
    private ArrayList<SpectatorPlayer> spectators = new ArrayList<SpectatorPlayer>();
    private List<LobbyPlayer> lobby = new ArrayList<LobbyPlayer>();
    private List<ArenaPlayer> inGame = new ArrayList<ArenaPlayer>();
    private boolean isMaxSet, isMinSet, isSpectateSet, isEnabled;
    private String name, currentName;

    public Arena(String name, String currentName) {
        this.currentName = currentName;
        this.name = name;

        for (Team t : ArenaManager.getArenaManager().getTeamsList(this)) {
            getArenaTeamList().add(t);
        }
    }

    public String getName() {
        return this.currentName;
    }

    private void setName(String newName) {
        FILE.set(getPath() + "Name", newName);
        this.currentName = newName;
    }

    public String getDefaultName() {
        return name;
    }

    public void removeArena() {
        FILE.set("Arenas." + this.getDefaultName(), null);
        ArenaManager.getArenaManager().getArenas().remove(this);
        advSave();
    }

    public void rename(String newName) {
        // Rename .Name to arenas new name
        FILE.set(getPath() + "Name", newName);

        // Rename the name in memory
        setName(newName);
        advSave();
    }

    public Location getSpawn(Team team) {
        return (Location) FILE.get(team.getPath() + ".Spawn");
    }

    public void setArenaSpawn(Location location, Team team) {
        FILE.set(team.getPath() + ".Spawn", location);
        advSave();
    }

    public void setLobbySpawn(Location location, Team team) {
        FILE.set(team.getPath() + ".Lobby", location);
        advSave();
    }

    public Location getLobbySpawn(Team team) {
        return (Location) FILE.get(team.getPath() + ".Lobby");
    }

    private Location getSpectateLoc() {
        return (Location) FILE.get(getPath() + spectatePath);
    }

    public void setSpectateLoc(Location loc) {
        isSpectateSet = true;
        FILE.set(getPath() + spectatePath, loc);
        advSave();
    }

    public void joinSpectate(Player player) {
        new SpectatorPlayer(this, player);
        // TODO: add check to command
        // TODO: set name color to gray
        /*if (this.containsPlayer(player)) {
            Message.getMessenger().msg(player, false, RED, "You are already in an arena!");
            return;
        }
        Settings.getSettings().getCache().savePlayerInformation(player);
        Utils.removePlayerSettings(player);
        player.teleport(getSpectateLoc());
        spectators.add(player);
        */
    }

    public void setMaxPlayers(int max) {
        isMaxSet = true;
        FILE.set(getPath() + maxPath, max);
        advSave();
    }

    public void setMinPlayers(int min) {
        isMinSet = true;
        FILE.set(getPath() + minPath, min);
        advSave();
    }

    public int getMax() {
        return FILE.getInt(getPath() + maxPath);
    }

    public int getMin() {
        return FILE.getInt(getPath() + minPath);
    }

    public List<Team> getArenaTeamList() {
        return ArenaManager.getArenaManager().getTeamsList(this);
    }

    public void setArenaTeamList(List<Team> teams) {
        List<String> teamColors = new ArrayList<String>();
        for (Team t : teams) {
            teamColors.add(t.getChatColor() + "");
        }
        Settings.getSettings().getArenaFile().set(getPath() + "Teams", teamColors);
        advSave();
    }

    public String getSteps() {
        ArrayList<String> steps = new ArrayList<String>();
        String finalString;
        ChatColor done = STRIKETHROUGH;
        String end = RESET + "" + GRAY;
        String prefix = BLUE + "Steps: ";

        steps = Utils.addItemsToArray(steps, isMaxSet ? done + "max" + end : "max", isMinSet ? done + "min" + end : "min");
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

    public boolean isSetup() {
        boolean spawnsSet = true;
        if (getArenaTeamList().isEmpty()) {
            spawnsSet = false;
        }
        for (Team t : getArenaTeamList()) {
            if (FILE.get(t.getPath() + ".Lobby") == null) {
                spawnsSet = false;
            }
            if (FILE.get(t.getPath() + ".Spawn") == null) {
                spawnsSet = false;
            }
        }
        return isMaxSet && isMinSet && isSpectateSet && spawnsSet;
    }

    public boolean containsPlayer(Player player) {
        // TODO: does this even work?
        return allPlayers.keySet().contains(player);
    }

    //public Team getTeam(Player player) {
    //  return lobby.keySet().contains(player) ? lobby.get(player) : inGame.get(getPbPlayer(player));
    //}

    public PaintballPlayer getPaintballPlayer(Player player) {
        return allPlayers.get(player);
    }

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

    public String toString() {
        return "Arena " + GRAY + this.getName() + GREEN;
    }

    public String getPath() {
        return "Arenas." + name + ".";
    }

    // TODO: move getTeamWithLessPlayers to lobby
    public void joinLobby(Player player, Team team) {
        new LobbyPlayer(this, team == null ? getTeamWithLessPlayers() : team, player);
    }

    public void startGame() {
        // TODO: Set all the player's walk speed, swim speed, and fly speed tpo 0
        state = ArenaState.IN_PROGRESS;
        for (LobbyPlayer p : lobby) {
            new ArenaPlayer(this, p.getTeam(), p.getPlayer());
        }
        lobby.removeAll(lobby);
        // TODO does this work?
        allPlayers.remove(lobby);
        Utils.countdown(Settings.ARENA_COUNTDOWN, Settings.ARENA_INTERVAL, Settings.ARENA_NO_INTERVAL, this, "Paintball starting in " + GRAY + "%time%" + GREEN + " seconds!", GREEN + "Starting\n" + GRAY + "%time%" + GREEN + " seconds", GREEN + "Game started!", false);
    }

    // Used for server reload and forcestops, so no messages will be sent
    public void forceRemovePlayers() {
        for (PaintballPlayer player : allPlayers.values()) {
            player.leaveArena();
            // player.forceLeave()?
        }
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

    public void win(Team team) {
        // TODO: add you won!
        broadcastMessage(GREEN, "The " + team.getTitleName() + GREEN + " has won!", "The " + team.getTitleName() + GREEN + " has won!");
    }

    public void broadcastMessage(ChatColor color, String chatMessage, String screenMessage) {
        for (PaintballPlayer pbPlayer : allPlayers.values()) {
            if (!screenMessage.equals("") && Settings.TITLE_API) {
                String[] messages = screenMessage.split("\n");
                TitleAPI.sendTitle(pbPlayer.getPlayer(), 10, 10, 10, messages.length == 1 ? Settings.getSettings().getPrefix() : messages[0], messages.length == 1 ? screenMessage : messages[1]);
            }
            pbPlayer.getPlayer().sendMessage(Settings.getSettings().getPrefix() + color + chatMessage);
        }
    }

    private Team getTeamWithLessPlayers() {
        // Make new HashMap with Team to Size, this way we can easily extract the largest size
        HashMap<Team, Integer> size = new HashMap<Team, Integer>();
        for (Team t : getArenaTeamList()) {
            // set the team size to 0
            size.put(t, 0);
            for (LobbyPlayer lobbyPlayer : lobby) {
                if (lobbyPlayer.getTeam().getTitleName().equals(t.getTitleName())) {
                    size.put(t, size.get(t) + 1);
                }
            }
        }
        return Utils.max(this, size);
    }

    public boolean canStartTimer() {
        int size = lobby.size();
        return size >= this.getMin() && size <= this.getMax();
    }

    public ArenaState getState() {
        if (!isSetup()) {
            state = ArenaState.NOT_SETUP;
        }
        return state;
    }

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

    public void updateAllSigns() {
        List<String> signLocs = Settings.getSettings().getArenaFile().getStringList(getPath() + "Sign-Locs");
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

    public void addLobbyPlayer(LobbyPlayer lobbyPlayer) {
        if (!allPlayers.values().contains(lobbyPlayer)) {
            allPlayers.put(lobbyPlayer.getPlayer(), lobbyPlayer);
        }
        lobby.add(lobbyPlayer);
    }

    public void addArenaPlayer(ArenaPlayer arenaPlayer) {
        if (!allPlayers.values().contains(arenaPlayer)) {
            allPlayers.put(arenaPlayer.getPlayer(), arenaPlayer);
        }
        inGame.add(arenaPlayer);
    }

    public void addSpectator(SpectatorPlayer spectatorPlayer) {
        if (!allPlayers.values().contains(spectatorPlayer)) {
            allPlayers.put(spectatorPlayer.getPlayer(), spectatorPlayer);
        }
        spectators.add(spectatorPlayer);
    }

    public List<LobbyPlayer> getLobbyPlayers() {
        return lobby;
    }

    public List<ArenaPlayer> getAllArenaPlayers() {
        return inGame;
    }

    public Map<Player, PaintballPlayer> getAllPlayers() {
        return allPlayers;
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

    public enum ArenaState {
        NOT_SETUP,
        WAITING,
        DISABLED,
        IN_PROGRESS
    }
}