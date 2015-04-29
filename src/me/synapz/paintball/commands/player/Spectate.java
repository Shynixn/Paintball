package me.synapz.paintball.commands.player;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.Command;
import org.bukkit.entity.Player;

public class Spectate extends Command{

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[1]);

        if (nullCheck(args[1], arena, player)) {
            arena.addToSpectate(player);
        }
    }

    public String getName() {
        return "spectate";
    }

    public String getInfo() {
        return "Spectate an arena.";
    }

    public String getArgs() {
        return "<arena>";
    }

    public String getPermission() {
        return "panintball.spectate";
    }

    public CommandType getCommandType() {
        return CommandType.PLAYER;
    }

    public int getMaxArgs() {
        return 2;
    }

    public int getMinArgs() {
        return 2;
    }
}
