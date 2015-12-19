package me.synapz.paintball.commands.admin;


import me.synapz.paintball.Arena;
import static me.synapz.paintball.Arena.ArenaState.*;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Message;
import me.synapz.paintball.Utils;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceStart extends Command{

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);

        if (Utils.nullCheck(args[2], arena, player)) {
            String error = "";
            switch (arena.getState()) {
                case WAITING:
                    if (arena.getLobbyPlayers().size() < arena.getMin()) {
                        error = "does not have enough players.";
                        break;
                    } else {
                        arena.forceStart(true);
                        return;
                    }
                case DISABLED:
                    error = "has not been enabled.";
                    break;
                case NOT_SETUP:
                    error = "has not been setup.";
                    break;
                case IN_PROGRESS:
                    error = "is already in progress";
                    break;
                default:
                    error = "has encountered an unexpected error.";
                    break;
            }
            Message.getMessenger().msg(player, false, ChatColor.RED, arena.toString() + ChatColor.RED + " " + error);
        }
    }

    public String getArgs() {
        String args = "<arena>";
        return args;
    }

    public String getPermission() {
        return "paintball.admin.start";
    }

    public String getName() {
        return "start";
    }

    public String getInfo() {
        return "Force start an Arena";
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
