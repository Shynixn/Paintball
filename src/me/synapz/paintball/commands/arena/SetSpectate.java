package me.synapz.paintball.commands.arena;


import me.synapz.paintball.Message;
import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;

public class SetSpectate extends ArenaCommand {

    public void onCommand() {
        arena.setSpectatorLocation(player.getLocation());
        Message.getMessenger().msg(player, false, ChatColor.GREEN, arena.toString() + ChatColor.GREEN + " spectate location set: " + Settings.SECONDARY + (Settings.ARENA_FILE.getConfigurationSection(arena.getPath() + "Spectator") == null ? 1 : Settings.ARENA_FILE.getConfigurationSection(arena.getPath() + "Spectator").getValues(false).size()), arena.getSteps());
    }

    public String getArgs() {
        String args = "<arena>";
        return args;
    }

    public String getPermission() {
        return "paintball.arena.setspectate";
    }

    public String getName() {
        return "setspec";
    }

    public String getInfo() {
        return "Set Arena spectate location";
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
