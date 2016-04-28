package me.synapz.paintball.commands.arena;


import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.ChatColor;

public class Remove extends ArenaCommand {

    public void onCommand() {
        boolean sendMessage = !arena.getAllPlayers().containsKey(player);

        arena.removeArena();

        if (sendMessage)
            Messenger.success(player, arena.toString(ChatColor.GREEN) + " successfully removed!");
    }

    public String getName() {
        return "remove";
    }

    public Messages getInfo() {
        return Messages.COMMAND_REMOVE_INFO;
    }

    public String getArgs() {
        return "<arena>";
    }

    public String getPermission() {
        return "paintball.arena.remove";
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
