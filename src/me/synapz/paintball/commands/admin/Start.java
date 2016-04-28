package me.synapz.paintball.commands.admin;


import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.ChatColor;

public class Start extends ArenaCommand {

    public void onCommand() {
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
                msg = "is already in progress.";
                break;
            case STOPPING:
                msg = "is already in progress.";
                break;
            case STARTING:
                msg = "is already in progress.";
                break;
            default:
                msg = "has encountered an unexpected error.";
                break;
            }
        Messenger.info(player, arena.toString(color) + " " + msg);
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

    public Messages getInfo() {
        return Messages.COMMAND_START_INFO;
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
