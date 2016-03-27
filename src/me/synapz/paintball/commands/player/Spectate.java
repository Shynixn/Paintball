package me.synapz.paintball.commands.player;

import me.synapz.paintball.Messenger;
import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import org.bukkit.ChatColor;

public class Spectate extends ArenaCommand {

    public void onCommand() {
        switch (arena.getState()) {
            case NOT_SETUP:
                Messenger.error(player, arena.toString(ChatColor.RED) + " has not been fully setup.");
                return;
            case DISABLED:
                Messenger.error(player, arena.toString(ChatColor.RED) + " is disabled.");
                return;
            case WAITING:
                Messenger.error(player, arena.toString(ChatColor.RED) + " is currently not in progress, nothing to spectate.");
                return;
            default:
                break;
        }

        if (arena.getAllPlayers().keySet().contains(player)) {
            Messenger.error(player, "You are already in an arena!");
            return;
        }

        arena.joinSpectate(player);
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
        return "paintball.spectate";
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

    protected int getArenaArg() {
        return 1;
    }
}
