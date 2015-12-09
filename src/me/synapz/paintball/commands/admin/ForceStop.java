package me.synapz.paintball.commands.admin;


import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Utils;
import me.synapz.paintball.commands.Command;
import org.bukkit.entity.Player;

public class ForceStop extends Command {

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);

        if (Utils.nullCheck(args[2], arena, player)) {
            arena.forceStop(player);
        }
    }

    public String getArgs() {
        String args = "<arena>";
        return args;
    }

    public String getPermission() {
        return "paintball.admin.stop";
    }

    public String getName() {
        return "stop";
    }

    public String getInfo() {
        return "Force stop an Arena";
    }

    public Command.CommandType getCommandType() {
        return Command.CommandType.ADMIN;
    }

    public int getMaxArgs() {
        return 3;
    }

    public int getMinArgs() {
        return 3;
    }
}
