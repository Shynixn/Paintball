package me.synapz.paintball;

import com.google.common.base.Joiner;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.locations.SignLocation;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;

import static me.synapz.paintball.storage.Settings.PLAYERDATA;
import static me.synapz.paintball.storage.Settings.THEME;
import static org.bukkit.ChatColor.*;
import static me.synapz.paintball.locations.SignLocation.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

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
            a.forceRemovePlayers();
        }
    }

    // Get a readable list and send it to the param player
    public void getList(Player player) {
        List<String> list = new ArrayList<String>();

        if (getArenas().size() == 0) {
            Message.getMessenger().msg(player, false, BLUE, "There are currently no arenas.");
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
        Message.getMessenger().msg(player, false, GRAY, BLUE + "Arenas: " + out,
                GREEN + "█-" + GRAY + "Joinable " + RED + "█-" + GRAY + "InProgress " + GRAY + "█-" + GRAY + "Disabled/Not-Setup");
    }

    // Updates every type of sign (Leaderboard, Join, Autojoin)
    public void updateAllSignsOnServer() {
        String prefix = DARK_GRAY + "[" + THEME + "Paintball" + DARK_GRAY + "]";

        for (Arena a : getArenas().values()) {
            a.updateSigns();
        }

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
                    // TODO: better way
                    // TODO: lb signs not updating :(

                    StatType type = null;
                    for (StatType t : StatType.values()) {
                        if (t.getSignName().equalsIgnoreCase(sign.getLine(2))) {
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