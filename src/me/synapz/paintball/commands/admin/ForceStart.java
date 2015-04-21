package me.synapz.paintball.commands.admin;


import me.synapz.paintball.Message;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ForceStart extends Command{

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);

        if (nullCheck(args[2], arena, player)) {
            arena.forceStart(player);
        }
    }

    public String getArgs() {
        String args = "<name>";
        return args;
    }

    public String getPermission() {
        return "paintball.admin.start";
    }

    public String getName() {
        return "start";
    }

    public String getInfo() {
        return "Force start an arena";
    }

    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    public int getMaxArgs() {
        return 3;
    }

    public int getMinArgs() {
        return 3;
    }
}
