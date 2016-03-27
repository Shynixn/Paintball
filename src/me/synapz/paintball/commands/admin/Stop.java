package me.synapz.paintball.commands.admin;


import me.synapz.paintball.Arena;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public class Stop extends ArenaCommand {

    public void onCommand() {
        if (arena.getState() == Arena.ArenaState.IN_PROGRESS || arena.getState() == Arena.ArenaState.STARTING || arena.getState() == Arena.ArenaState.STOPPING) {
            // if the player isn't in the arena send them a message, otherwise the forceStart method will send the message to everyone
            if (!arena.getAllPlayers().keySet().contains(player))
                Messenger.success(player, arena.toString(GREEN) + " has been force stopped!");
            arena.forceStart(false);
            return;
        }
        Messenger.error(player, arena.toString(RED) + " is not in progress.");
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

    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    public int getMaxArgs() {
        return 3;
    }

    public int getMinArgs() {
        return 3;
    }

    protected int getArenaArg() {
        return 2;
    }
}
