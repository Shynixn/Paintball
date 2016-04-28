package me.synapz.paintball.commands.admin;

import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.ChatColor;

public class Convert extends ArenaCommand {

    // pb admin convert <arena> <type>

    public void onCommand() {
        ArenaType type = ArenaType.getArenaType(player, args[3]);

        if (type == null)
            return;

        if (type == arena.getArenaType()) {
            Messenger.error(player, arena.toString(ChatColor.RED) + " is already a " + type.getShortName().toUpperCase() + " arena.");
        } else {
            arena.setArenaType(type);
            Messenger.success(player, arena.toString(ChatColor.GREEN) + " has been converted to " + type.getFullName());
        }
    }

    public String getArgs() {
        String args = "<arena> <" + ArenaType.getReadableList() + ">";
        return args;
    }

    public String getPermission() {
        return "paintball.admin.convert";
    }

    public String getName() {
        return "convert";
    }

    public Messages getInfo() {
        return Messages.COMMAND_CONVERT_INFO;
    }

    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    public int getMaxArgs() {
        return 4;
    }

    public int getMinArgs() {
        return 4;
    }

    protected int getArenaArg() {
        return 2;
    }
}
