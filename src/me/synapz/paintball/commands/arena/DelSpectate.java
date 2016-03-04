package me.synapz.paintball.commands.arena;

import me.synapz.paintball.Messenger;
import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;

public class DelSpectate extends ArenaCommand {

    public void onCommand() {
        if (Settings.ARENA_FILE.getConfigurationSection(arena.getPath() + "Spectator") == null || Settings.ARENA_FILE.getConfigurationSection(arena.getPath() + "Spectator").getValues(false).size() <= 0) {
            Messenger.error(player, "There are no spectator spawns to be deleted.");
            return;
        }

        arena.removeSpectatorLocation();
        Messenger.success(player, arena.toString() + ChatColor.GREEN + " spectate location deleted: " + Settings.SECONDARY + (Settings.ARENA_FILE.getConfigurationSection(arena.getPath() + "Spectator") == null ? 1 : Settings.ARENA_FILE.getConfigurationSection(arena.getPath() + "Spectator").getValues(false).size()), arena.getSteps());
    }

    public String getArgs() {
        String args = "<arena>";
        return args;
    }

    public String getPermission() {
        return "paintball.arena.delspec";
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
