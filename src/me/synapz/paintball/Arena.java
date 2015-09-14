package me.synapz.paintball;


import com.google.common.base.Joiner;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class Arena {
    
    private FileConfiguration FILE = Settings.getSettings().getArenaFile();
    
    private ArrayList<Player> spectators = new ArrayList<Player>();
    private Map<Player, Team> lobby = new HashMap<Player, Team>();
    private HashMap<PbPlayer, Team> inGame = new HashMap<PbPlayer, Team>();
    
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
        IN_PROGRESS;
    }
    
    public Arena(String name, String currentName) {
        this.currentName = currentName;
        this.name = name;
        
        for (Team t : ArenaManager.getArenaManager().getTeamsList(this)) {
            getArenaTeamList().add(t);
        }
    }
    
    public String getName() {
        return currentName;
    }
    
    public String getDefaultName() {
        return name;
    }
    
    private void setName(String newName) {
        FILE.set(getPath() + "Name", newName);
        currentName = newName;
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
    
    private Location getSpawn(Team team) {
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
    
    public void setSpectateLoc(Location loc) {
        isSpectateSet = true;
        FILE.set(getPath() + spectatePath, loc);
        advSave();
    }
    
    private Location getSpectateLoc() {
        return (Location) FILE.get(getPath() + spectatePath);
    }
    
    public void joinSpectate(Player player) {
        // TODO: set name color to gray
        if (this.containsPlayer(player)) {
            Message.getMessenger().msg(player, RED, "You are already in an arena!");
            return;
        }
        Settings.getSettings().getCache().savePlayerInformation(player);
        Utils.removePlayerSettings(player);
        player.teleport(getSpectateLoc());
        spectators.add(player);
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
        
        steps = Utils.addItemsToArray(steps, isMaxSet ? done + "max"+end : "max", isMinSet ? done + "min"+end : "min");
        for (Team t : getArenaTeamList()) {
            steps.add(FILE.get(t.getPath() + ".Lobby") != null ? done + t.getTitleName().toLowerCase() + "-lobby" + end : t.getTitleName().toLowerCase() + "-lobby");
            steps.add(FILE.get(t.getPath() + ".Spawn") != null ? done + t.getTitleName().toLowerCase() + "-spawn"+ end : t.getTitleName().toLowerCase() + "-spawn");
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
        Message.getMessenger().msg(sender, color, this.toString() + " " + message);
    }
    
    public boolean isSetup() {
        boolean spawnsSet = true;
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
    
    public Team getTeam(Player player) {
        return lobby.keySet().contains(player) ? lobby.get(player) : inGame.get(getPbPlayer(player));
    }
    
    public boolean containsPlayer(Player player) {
        return lobby.keySet().contains(player) || getPbPlayer(player) != null && inGame.keySet().contains(getPbPlayer(player)) || spectators.contains(player);
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
        String reason = "";
        if (isSetup()) {
            if (lobby.keySet().size() >= getMin()) {
                if (state == ArenaState.WAITING) {
                    startGame();
                    Message.getMessenger().msg(sender, GREEN, "Successfully started " + this.toString());
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
        Message.getMessenger().msg(sender, RED, "Cannot force start " + name, this.toString() + RED + reason);
    }
    
    public void forceStop(Player sender) {
        if (state == ArenaState.IN_PROGRESS) {
            this.removePlayers();
            broadcastMessage(RED, this.toString() + RED + " has been force stopped.");
            // todo make config value to compleelty block all commands
            if (!this.containsPlayer(sender)) {
                Message.getMessenger().msg(sender, GREEN, this.toString() + " has been force stopped.");
            }
            return;
        }
        Message.getMessenger().msg(sender, RED, "Cannot force stop " + this.toString(), this.toString() + RED + " is not in progress.");
    }
    
    public String toString() {
        return "Arena " + GRAY + this.getName() + GREEN;
    }
    
    
    public String getPath() {
        return "Arenas." + name + ".";
    }
    
    
    public void joinLobby(Player player, Team team) {
        team = team == null ? getTeamWithLessPlayers() : team;
        lobby.put(player, team);
        Settings.getSettings().getCache().savePlayerInformation(player);
        Utils.removePlayerSettings(player);
        
        player.teleport(getLobbySpawn(team));
        broadcastMessage(GREEN, team.getChatColor() + player.getName() + GREEN + " has joined the arena! " + GRAY + lobby.keySet().size() + "/" + this.getMax());
        
        if (canStart()) {
            this.startGame();
        }
    }
    
    public void chat(Player player, String message) {
        String chat = Settings.ARENA_CHAT;
        if (spectators.contains(player)) {
            chat = Settings.SPEC_CHAT;
        } else {
            chat = chat.replace("%TEAMNAME%", getTeam(player).getTitleName());
            chat = chat.replace("%TEAMCOLOR%", getTeam(player).getChatColor() + "");
        }
        chat = chat.replace("%MSG%", message);
        chat = chat.replace("%PREFIX%", Settings.getSettings().getPrefix());
        chat = chat.replace("%PLAYER%", player.getName());
        for (Player p : lobby.keySet()) {
            p.sendMessage(chat);
        }
        for (Player p : spectators) {
            p.sendMessage(chat);
        }
        for (PbPlayer pb : inGame.keySet()) {
            pb.getPlayer().sendMessage(chat);
        }
    }
    
    private void startGame() {
        // Set all the player's walk speed, swim speed, and fly speed tpo 0
        Utils.countdown(this, Settings.COUNTDOWN, inGame.keySet());
        state = ArenaState.IN_PROGRESS;
        for (Player p : lobby.keySet()) {
            this.addPlayerToArena(p);
        }
        lobby.keySet().removeAll(lobby.keySet());
    }
    
    // Used for server reload and forcestops, so no messages will be sent
    public void removePlayers() {
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
        state = ArenaState.WAITING;
    }
    
    public void leave(Player player) {
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
        if (inGame.keySet().isEmpty()) {
            // in case min players is set to 0, when a player leaves arena doesn't get reset
            state = ArenaState.WAITING;
        }
    }
    
    public void win(Team team) {
        broadcastMessage(GREEN, "The " + team.getTitleName() + GREEN + " has won!");
    }
    
    public PbPlayer getPbPlayer(Player player) {
        for (PbPlayer pbPlayer : inGame.keySet()) {
            if (pbPlayer.getName().equals(player.getName())) {
                return pbPlayer;
            }
        }
        return null;
    }
    
    private void addPlayerToArena(Player player) {
        Team team = getTeam(player);
        PbPlayer pbPlayer = new PbPlayer(player, team, this);
        inGame.put(pbPlayer, team);
        player.teleport(getSpawn(team));
    }
    
    public void broadcastMessage(ChatColor color, String...messages) {
        for (PbPlayer pb : inGame.keySet()) {
            for (String message : messages) {
                Bukkit.getServer().getPlayer(pb.getName()).sendMessage(Settings.getSettings().getPrefix() + color + message);
            }
        }
        for (Player p : lobby.keySet()) {
            for (String message : messages) {
                p.sendMessage(Settings.getSettings().getPrefix() + color + message);
            }
        }
    }
    
    private Team getTeamWithLessPlayers() {
        // Make new HashMap with Team to Size, this way we can easily extract the largest size
        HashMap<Team, Integer> size = new HashMap<Team, Integer>();
        for (Team t : getArenaTeamList()) {
            // set the team size to 0
            size.put(t, 0);
            for (Player p : lobby.keySet()) {
                if (getTeam(p).getTitleName().equals(t.getTitleName())) {
                    size.put(t, size.get(t)+1);
                }
            }
        }
        
        return Utils.max(this, size);
    }
    
    private boolean canStart() {
        int size = lobby.keySet().size();
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
                sign.setLine(3, this.inGame.keySet().size() + this.lobby.keySet().size() + "/" + this.getMax());
                sign.update();
            } else {
                ArenaManager.getArenaManager().removeSignLocation(loc, this);
            }
        }
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