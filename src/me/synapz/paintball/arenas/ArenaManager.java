package me.synapz.paintball.arenas;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.base.Joiner;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.locations.SignLocation;
import me.synapz.paintball.locations.SkullLocation;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import java.util.*;

import static org.bukkit.ChatColor.*;

public class ArenaManager {

    private static ArenaManager instance = new ArenaManager();
    // HashMap with arena's name to arena, makes it way more efficient to get an arena from a string
    private Map<String, Arena> arenas = new HashMap<>();

    private ArenaManager() {
    }

    public static ArenaManager getArenaManager() {
        return instance;
    }

    // Gets an arena from a name
    public Arena getArena(String name) {
        Arena arena = arenas.get(name);

        if (arena == null) {
            for (String rawName : arenas.keySet()) {
                if (name.equalsIgnoreCase(rawName)) {
                    return arenas.get(rawName);
                }
            }
            return null;
        } else {
            return arena;
        }
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
    public Map<String, Arena> getArenas() {
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
            Messenger.info(player, Messages.NO_ARENAS);
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


        Messenger.info(player, new MessageBuilder(Messages.ARENA_LIST_COMMAND).replace(Tag.ARENA, out).build(),
                GREEN + "█-" + GRAY + Messages.WAITING.getString() + RED + " █-" + GRAY + Messages.PLAYING.getString() + GRAY + " █-" + GRAY + Messages.DISABLED.getString());
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
        String prefix = Messages.SIGN_TITLE.getString();

        Paintball.getInstance().getBungeeManager().updateBungeeSigns();

        for (SignLocation signLoc : Settings.ARENA.getSigns().values()) {
            if (!(signLoc instanceof SkullLocation) && !(signLoc.getLocation().getBlock().getState() instanceof Sign)) {
                signLoc.removeSign();
                return;
            }

            BlockState state = signLoc.getLocation().getBlock().getState();
            switch (signLoc.getType()) {
                case AUTOJOIN:
                    if (state instanceof Sign) {
                        Sign sign = (Sign) state;

                        sign.setLine(0, prefix); // in case the prefix changes
                        sign.update();
                    }
                    break;
                case LEADERBOARD:
                    if (state instanceof Sign) {
                        Sign sign = (Sign) state;

                        StatType type = StatType.getStatType(null, sign.getLine(2));

                        if (type == null) {
                            signLoc.removeSign();
                            return;
                        }

                        Map<String, String> playerAndStat;

                        try {
                            playerAndStat = Settings.getSettings().getStatsFolder().getPlayerAtRank(Integer.parseInt(sign.getLine(0).replace("#", "")), type);
                        } catch (NumberFormatException exc) {
                            continue;
                        }

                        sign.setLine(1, playerAndStat.keySet().toArray()[0] + "");
                        sign.setLine(3, playerAndStat.values().toArray()[0] + "");
                        sign.update();
                    }
                    break;
                case SKULL:
                    if (state instanceof Skull && signLoc instanceof SkullLocation) {
                        SkullLocation skullLoc = (SkullLocation) signLoc;
                        Skull skull = (Skull) state;

                        skullLoc.makeSkullBlock(skull.getRotation());
                    }
                    break;
                default:
                    break; // should never happen
            }
        }

        if (Settings.HOLOGRAPHIC_DISPLAYS) {
            Collection<Hologram> holograms = HologramsAPI.getHolograms(Paintball.getInstance());

            for (Hologram holo : holograms) {
                String line = ChatColor.stripColor(holo.getLine(0).toString());
                StatType type = null;
                int page;

                if (holo.getLine(0).toString().contains("Top")) {
                    page = Integer.parseInt(line.split(" ")[14]);
                } else {
                    type = StatType.getStatType(null, line.split(" ")[13]);
                    page = Integer.parseInt(line.split(" ")[15].split("/")[0]);
                }
                holo.clearLines();

                for (String statLine : Settings.getSettings().getStatsFolder().getPage(type, page)) {
                    holo.appendTextLine(statLine);
                }
            }
        }
    }
}