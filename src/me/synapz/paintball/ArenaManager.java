package me.synapz.paintball;

import com.google.common.base.Joiner;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.locations.SignLocation;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.synapz.paintball.storage.Settings.PLAYERDATA;
import static me.synapz.paintball.storage.Settings.THEME;
import static org.bukkit.ChatColor.*;

public class ArenaManager {

    private ArenaManager() {}

    private static ArenaManager instance = new ArenaManager();

    // HashMap with arena's name to arena, makes it way more efficient to get an arena from a string
    private HashMap<String, Arena> arenas = new HashMap<>();

    public static ArenaManager getArenaManager() {
        return instance;
    }

    // Gets an arena from a name
    public Arena getArena(String name) {
        return arenas.get(name);
    }

    // Gets an arena from a player inside it
    public Arena getArena(Player player) {
        for (Arena a : arenas.values()) {
            if (a.containsPlayer(player))
                return a;
        }
        return null;
    }

    // Gets a list of all arenas
    public HashMap<String, Arena> getArenas() {
        return arenas;
    }

    // Stops all arenas
    public void stopArenas() {
        for (Arena a : getArenas().values()) {
            a.stopGame();
        }
    }

    // Get a readable list and send it to the param player
    public void getList(Player player) {
        List<String> list = new ArrayList<String>();

        if (getArenas().size() == 0) {
            Messenger.info(player, BLUE + "There are currently no arenas.");
            return;
        }

        for (Arena a : getArenas().values()) {
            String color = "";

            switch (a.getState()) {
                case WAITING:
                    color += GREEN;
                    break;
                case IN_PROGRESS:
                    color += RED;
                    break;
                case STOPPING:
                    color += RED;
                case STARTING:
                    color += RED;
                case DISABLED:
                    color += GRAY;
                    break;
                case NOT_SETUP:
                    color += STRIKETHROUGH + "" + GRAY;
                    break;
                default:
                    color += RED;
                    break;
            }
            list.add(ChatColor.RESET + "" + color + a.getName());
        }

        String out = Joiner.on(GRAY + ", ").join(list);
        Messenger.info(player, BLUE + "Arenas: " + out,
                GREEN + "█-" + GRAY + "Joinable " + RED + "█-" + GRAY + "InProgress " + GRAY + "█-" + GRAY + "Disabled/Not-Setup");
    }

    public Arena getBestArena() {
        int currentSize = -1;
        Arena greatestSizeArena = null;
        for (Arena arena : getArenas().values()) {
            if (arena.getState() == Arena.ArenaState.WAITING && currentSize < arena.getLobbyPlayers().size()) {
                greatestSizeArena = arena;
                currentSize = arena.getLobbyPlayers().size();
            }
        }
        for (Arena arena : getArenas().values()) {
            if (arena.getState() == Arena.ArenaState.WAITING && currentSize < arena.getLobbyPlayers().size()) {
                greatestSizeArena = arena;
                currentSize = arena.getLobbyPlayers().size();
            }
        }
        return greatestSizeArena;
    }

    // Updates every type of sign (Leaderboard, Join, Autojoin)
    public void updateAllSignsOnServer() {
        String prefix = DARK_GRAY + "[" + THEME + "Paintball" + DARK_GRAY + "]";

        for (SignLocation signLoc : Settings.ARENA.getSigns().values()) {
            if (!(signLoc.getLocation().getBlock().getState() instanceof Sign)) {
                signLoc.removeSign();
                return;
            }

            Sign sign = (Sign) signLoc.getLocation().getBlock().getState();
            switch (signLoc.getType()) {
                case AUTOJOIN:
                    sign.setLine(0, prefix); // in case the prefix changes
                    sign.update();
                    break;
                case LEADERBOARD:
                    StatType type = null;
                    for (StatType t : StatType.values()) {
                        if (t.getSignName().equalsIgnoreCase(sign.getLine(2).replace(" ", ""))) {
                            type = t;
                        }
                    }
                    if (type == null) {
                        signLoc.removeSign();
                        return;
                    }
                    HashMap<String, String> playerAndStat = PLAYERDATA.getPlayerAtRank(Integer.parseInt(sign.getLine(0).replace("#", "")), type);
                    sign.setLine(1, playerAndStat.keySet().toArray()[0] + "");
                    sign.setLine(3, playerAndStat.values().toArray()[0] + "");
                    sign.update();
                    break;
                default:
                    break; // should never happen
            }
        }
    }
}