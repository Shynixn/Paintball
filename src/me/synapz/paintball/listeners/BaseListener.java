package me.synapz.paintball.listeners;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.entity.Player;

public class BaseListener {

    // Returns true if they are in an arena, and false if they aren't, and also sends the player the error message if they are in the arena
    protected boolean stopAction(Player player, String message) {
        Arena a = ArenaManager.getArenaManager().getArena(player);
        if (a != null) {
            Messenger.error(player, message);
            return true;
        }
        return false;
    }

    protected boolean isInArena(Player player) {
        return getArena(player) != null;
    }

    protected Arena getArena(Player player) {
        return ArenaManager.getArenaManager().getArena(player);
    }
}
