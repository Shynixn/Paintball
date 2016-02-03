package me.synapz.paintball.commands.arena;


import me.synapz.paintball.Message;
import me.synapz.paintball.commands.ArenaCommand;

import me.synapz.paintball.enums.CommandType;
import org.bukkit.ChatColor;

public class RemoveArena extends ArenaCommand {

    public void onCommand() {
        arena.removeArena();
        Message.getMessenger().msg(player, false, ChatColor.GREEN, arena.toString() + " successfully removed!");
    }

    public String getName() {
        return "remove";
    }

    public String getInfo() {
        return "Remove an arena";
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
