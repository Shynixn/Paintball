package me.synapz.paintball.commands.admin;


import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public class Stop extends ArenaCommand {

    public void onCommand() {
        if (arena.getState() == Arena.ArenaState.IN_PROGRESS || arena.getState() == Arena.ArenaState.STARTING || arena.getState() == Arena.ArenaState.STOPPING) {
            // if the player isn't in the arena send them a message, otherwise the forceStart method will send the message to everyone
            if (!arena.getAllPlayers().keySet().contains(player))
                Messenger.success(player, new MessageBuilder(Messages.ARENA_FORCE_STOPPED).replace(Tag.ARENA, arena.toString(GREEN)).build());
            arena.forceStart(false);
            return;
        }
        Messenger.error(player, new MessageBuilder(Messages.ARENA_NOT_IN_PROGRESS).replace(Tag.ARENA, arena.toString(RED)).build());
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

    public Messages getInfo() {
        return Messages.COMMAND_STOP_INFO;
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
