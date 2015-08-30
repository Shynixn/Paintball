package me.synapz.paintball;

import java.util.ArrayList;

public class Team {

    public static ArrayList<Team> TEAMS = new ArrayList<Team>();

    private String color, path;
    private int id;
    private ArrayList<PbPlayer> playersInArena = new ArrayList<PbPlayer>();
    private ArrayList<String> lobbyPlayers = new ArrayList<String>();

    public Team (String path, int id, String color) {
        this.path = path;
        this.id = id;
        this.color = color;
    }

    public static void removeAllTeamsForReload() {
        TEAMS.removeAll(TEAMS);
    }

    public String getPath() {
        return path;
    }

    public String getChatColor() {
        return color;
    }

    public ArrayList<PbPlayer> getPlayersInArena() {
        return playersInArena;
    }

    public void removePlayerInArena(PbPlayer... pb) {
        for (PbPlayer p : pb) {
            playersInArena.remove(p);
        }
    }

    public void addPlayerInArena(PbPlayer... pb) {
        for (PbPlayer p : pb) {
            playersInArena.add(p);
        }
    }

    public ArrayList<String> getLobbyPlayers() {
        return lobbyPlayers;
    }

    public void removeLobbyPlayers(String... p) {
        for (String pl : p) {
            lobbyPlayers.remove(pl);
        }
    }

    public void addPlayerToLobby(String... p) {
        for (String pl : p) {
            lobbyPlayers.add(pl);
        }
    }

    public void removeAllPlayers() {
        playersInArena.removeAll(playersInArena);
    }

    public void removeAllLobbyPlayers() {
        lobbyPlayers.removeAll(lobbyPlayers);
    }

    public static void addNewTeam(Team t) {
        TEAMS.add(t);
    }

    public int getId() {
        return id;
    }

    public String getTitleName() {
        String color = "error";
        String[] list = new String[] {"§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§0", "§a", "§b", "§c", "§d", "§e", "§f"};
        String[] names = new String[] {"Blue", "Green", "Aqua", "Red", "Purple", "Yellow", "LightGray", "Gray", "LightBlue", "Black", "LightGreen", "LightAqua", "LightRed", "LightPurple", "LightYellow"};
        int i = 0;
        for (String s : list) {
            if (s.toCharArray()[1] == getChatColor().toCharArray()[1]) {
                color = names[i];
                break;
            }
            i++;
        }
        return color;
    }
}
