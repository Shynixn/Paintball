package me.synapz.paintball.commands.arena;

import me.synapz.paintball.Message;
import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;

public class DelSpectate extends ArenaCommand {

    public void onCommand() {
        arena.removeSpectatorLocation();
        Message.getMessenger().msg(player, false, ChatColor.GREEN, arena.toString() + ChatColor.GREEN + " spectate location deleted: " + (Settings.ARENA_FILE.getConfigurationSection(arena.getPath() + "Spectator") == null ? 1 : Settings.ARENA_FILE.getConfigurationSection(arena.getPath() + "Spectator").getValues(false).size()), arena.getSteps());
    }

    public String getArgs() {
        String args = "<arena>";
        return args;
    }

    public String getPermission() {
        return "paintball.arena.delspectate";
    }

    public String getName() {
        return "delspec";
    }

    public String getInfo() {
        return "Delete Arena spectate location";
    }

    public CommandType getCommandType() {
        return CommandType.ARENA;
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