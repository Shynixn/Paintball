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

    // Make Team chats
    private ArrayList<String> spectators = new ArrayList<String>();
    private FileConfiguration file = Settings.getSettings().getArenaFile();
    private HashMap<String, Team> lobby = new HashMap<String, Team>();
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
        return (Location) file.get(team.getPath() + ".Spawn");
    }

    public void setArenaSpawn(Location location, Team team) {
        file.set(team.getPath() + ".Spawn", location);
        advSave();
    }

    public void setLobbySpawn(Location location, Team team) {
        file.set(team.getPath() + ".Lobby", location);
        advSave();
    }

    public Location getLobbySpawn(Team team) {
        return (Location) file.get(team.getPath() + ".Lobby");
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
        if (this.containsPlayer(player)) {
            Message.getMessenger().msg(player, ChatColor.RED, "You are already in an arena!");
            return;
        }
        Settings.getSettings().getCache().savePlayerInformation(player);
        Utils.removePlayerSettings(player);
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

    public List<Team> getArenaTeamList() {
        return ArenaManager.getArenaManager().getTeamsList(this);
    }

    public void setArenaTeamList(List<Team> teams) {
        List<String> teamColors = new ArrayList<String>();
        for (Team t : teams) {
            teamColors.add(t.getChatColor());
        }
        Settings.getSettings().getArenaFile().set(getPath() + "Teams", teamColors);
        advSave();
    }

    public String getSteps() {
        ArrayList<String> steps = new ArrayList<String>();
        String finalString = "";
        ChatColor done = ChatColor.STRIKETHROUGH;
        String end = ChatColor.RESET + "" + ChatColor.GRAY;
        String prefix = ChatColor.BLUE + "Steps: ";

        steps = Utils.addItemsToArray(steps, isMaxSet ? done + "max"+end : "max", isMinSet ? done + "min"+end : "min");
        for (Team t : getArenaTeamList()) {
            steps.add(file.get(t.getPath() + ".Lobby") != null ? done + t.getTitleName().toLowerCase() + "-lobby" + end : t.getTitleName().toLowerCase() + "-lobby");
            steps.add(file.get(t.getPath() + ".Spawn") != null ? done + t.getTitleName().toLowerCase() + "-spawn"+ end : t.getTitleName().toLowerCase() + "-spawn");
        }
        Utils.addItemsToArray(steps, isSpectateSet ? done + "spectate" + end : "spectate", isEnabled ? done + "enable" + end : "enable", getArenaTeamList().isEmpty() ? "set-teams" : "");

        for (String step : steps) {
            finalString += ChatColor.GRAY + step + ", ";
        }
        return isSetup() && isEnabled ? prefix + ChatColor.GRAY + "Complete. Arena is open!" : prefix + finalString.substring(0, finalString.lastIndexOf(","));

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
        for (Team t : getArenaTeamList()) {
            if (file.get(t.getPath() + ".Lobby") == null) {
                spawnsSet = false;
            }
            if (file.get(t.getPath() + ".Spawn")== null) {
                spawnsSet = false;
            }
        }
        return isMaxSet && isMinSet && isSpectateSet && spawnsSet;
    }

    public Team getTeam(Player player) {
        return lobby.keySet().contains(player.getName()) ? lobby.get(player.getName()) : inGame.get(getPbPlayer(player));
    }

    public boolean containsPlayer(Player player) {
        return lobby.keySet().contains(player.getName()) || getPbPlayer(player) != null && inGame.keySet().contains(getPbPlayer(player)) || spectators.contains(player.getName());
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
        if (isSetup()) {
            if (lobby.keySet().size() >= getMin()) {
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
        // TODO: Add wool helmet on join lobby and give wool blocks to choose team
        team = team == null ? getTeamWithLessPlayers() : team;
        lobby.put(player.getName(), team);
        Settings.getSettings().getCache().savePlayerInformation(player);
        Utils.removePlayerSettings(player);

        player.teleport(getLobbySpawn(team));
        broadcastMessage(ChatColor.GREEN, team.getChatColor() + player.getName() + ChatColor.GREEN + " has joined the arena! " + ChatColor.GRAY + lobby.keySet().size() + "/" + this.getMax());

        if (canStart()) {
            this.startGame();
        }
    }

    public void chat(Player player, String message) {
        String chat = Settings.ARENA_CHAT;
        if (spectators.contains(player.getName())) {
            chat = Settings.SPEC_CHAT;
        } else {
            chat = chat.replaceAll("%TEAMNAME%", getTeam(player).getTitleName());
            chat = chat.replaceAll("%TEAMCOLOR%", getTeam(player).getChatColor());
        }
        chat = chat.replaceAll("%MSG%", message);
        chat = chat.replaceAll("%PREFIX%", Settings.getSettings().getPrefix());
        chat = chat.replaceAll("%PLAYER%", player.getName());
        for (String s : lobby.keySet()) {
            Bukkit.getPlayer(s).sendMessage(chat);
        }
        for (String s : spectators) {
            Bukkit.getPlayer(s).sendMessage(chat);
        }
        for (PbPlayer pb : inGame.keySet()) {
            pb.getPlayer().sendMessage(chat);
        }
    }

    private void startGame() {
        // Set all the player's walk speed, swim speed, and fly speed tpo 0

        Utils.countdown(this, Settings.COUNTDOWN, inGame.keySet());
        state = ArenaState.IN_PROGRESS;
        for (String p : lobby.keySet()) {
            this.addPlayerToArena(Bukkit.getPlayer(p));
        }
        lobby.keySet().removeAll(lobby.keySet());
    }

    // Used for server reload and forcestops, so no messages will be sent
    public void removePlayers() {
        for (PbPlayer pb : inGame.keySet()) {
            Settings.getSettings().getCache().restorePlayerInformation(pb.getPlayer().getUniqueId());
        }
        for (String p : lobby.keySet()) {
            Settings.getSettings().getCache().restorePlayerInformation(Bukkit.getPlayer(p).getUniqueId());
        }
        for (String p : spectators) {
            Settings.getSettings().getCache().restorePlayerInformation(Bukkit.getPlayer(p).getUniqueId());
        }
        lobby.keySet().removeAll(lobby.keySet());
        inGame.keySet().removeAll(inGame.keySet());
        spectators.removeAll(spectators);
        state = ArenaState.WAITING;
    }

    public void leave(Player player) {
        lobby.keySet().remove(player.getName());
        inGame.keySet().remove(getPbPlayer(player));
        spectators.remove(player.getName());
        Settings.getSettings().getCache().restorePlayerInformation(player.getUniqueId());

        if (inGame.keySet().size() == 1) {
            PbPlayer pbPlayer = (PbPlayer) inGame.keySet().toArray()[0];
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
        broadcastMessage(ChatColor.GREEN, "The " + team.getTitleName() + ChatColor.GREEN + " has won!");
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
        for (String name : lobby.keySet()) {
            for (String message : messages) {
                Bukkit.getServer().getPlayer(name).sendMessage(Settings.getSettings().getPrefix() + color + message);
            }
        }
    }

    private Team getTeamWithLessPlayers() {
        // Make new HashMap with Team to Size, this way we can easily extract the largest size
        HashMap<Team, Integer> size = new HashMap<Team, Integer>();
        for (Team t : getArenaTeamList()) {
            // set the team size to 0
            size.put(t, 0);
            for (String p : lobby.keySet()) {
                if (getTeam(Bukkit.getPlayer(p)).getTitleName().equals(t.getTitleName())) {
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
        ChatColor color = ChatColor.RED;
        switch (state) {
            case DISABLED:
                color = ChatColor.GRAY;
                break;
            case WAITING:
                color = ChatColor.GREEN;
                break;
            case IN_PROGRESS:
                color = ChatColor.RED;
                break;
            case NOT_SETUP:
                color = ChatColor.GRAY;
                break;
        }
        return color + state.toString();
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
