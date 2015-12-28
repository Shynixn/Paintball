package me.synapz.paintball.commands.admin;


import me.synapz.paintball.*;

import static me.synapz.paintball.Arena.ArenaState.*;

import me.synapz.paintball.commands.Command;
import static org.bukkit.ChatColor.*;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceStart extends Command{

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);

        if (Utils.nullCheck(args[2], arena, player)) {
            String msg;
            ChatColor color = ChatColor.RED;
            switch (arena.getState()) {
                case WAITING:
                    if (arena.getLobbyPlayers().size() < arena.getMin()) {
                        msg = "does not have enough players.";
                        break;
                    } else {
                        color = ChatColor.GREEN;
                        msg = "has been force started!";
                        arena.forceStart(true);
                        break;
                    }
                case DISABLED:
                    msg = "has not been enabled.";
                    break;
                case NOT_SETUP:
                    msg = "has not been setup.";
                    break;
                case IN_PROGRESS:
                    msg = "is already in progress";
                    break;
                default:
                    msg = "has encountered an unexpected error.";
                    break;
            }
            Message.getMessenger().msg(player, false, color, arena.toString() + color + " " + msg);
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
