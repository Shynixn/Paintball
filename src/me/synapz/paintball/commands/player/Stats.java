package me.synapz.paintball.commands.player;

import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.storage.Settings;
import org.bukkit.entity.Player;

public class Stats extends PaintballCommand {

    public void onCommand(Player player, String[] args) {
        String targetName = args.length == 1 ? player.getName() : args[1];

        Settings.getSettings().getStatsFolder().getStats(player, targetName);
    }

    public String getName() {
        return "stats";
    }

    public Messages getInfo() {
        return Messages.COMMAND_STATS_INFO;
    }

    public String getArgs() {
        return "[player]";
    }

    public String getPermission() {
        return "paintball.stats";
    }

    public CommandType getCommandType() {
        return CommandType.PLAYER;
    }

    public int getMaxArgs() {
        return 2;
    }

    public int getMinArgs() {
        return 1;
    }
}
