package me.synapz.paintball.commands.player;

import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.utils.Utils;

public class Spectate extends ArenaCommand {

    public void onCommand() {
        if (Utils.canJoinSpectate(arena, player))
            arena.joinSpectate(player);
    }

    public String getName() {
        return "spectate";
    }

    public Messages getInfo() {
        return Messages.COMMAND_SPECTATE_INFO;
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