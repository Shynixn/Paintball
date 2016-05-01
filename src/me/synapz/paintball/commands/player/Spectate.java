package me.synapz.paintball.commands.player;

import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.ChatColor;

public class Spectate extends ArenaCommand {

    public void onCommand() {
        switch (arena.getState()) {
            case NOT_SETUP:
                Messenger.error(player, new MessageBuilder(Messages.ARENA_NOT_SETUP).replace(Tag.ARENA, arena.toString(ChatColor.RED)).build());
                return;
            case DISABLED:
                Messenger.error(player, new MessageBuilder(Messages.ARENA_DISABLED).replace(Tag.ARENA, arena.toString(ChatColor.RED)).build());
                return;
            case WAITING:
                Messenger.error(player, new MessageBuilder(Messages.ARENA_NOT_IN_PROGRESS).replace(Tag.ARENA, arena.toString(ChatColor.RED)).build());
                return;
            default:
                break;
        }

        if (arena.getAllPlayers().keySet().contains(player)) {
            Messenger.error(player, Messages.IN_ARENA);
            return;
        }

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