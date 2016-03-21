package me.synapz.paintball.commands.player;

import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import org.bukkit.entity.Player;

import static me.synapz.paintball.storage.Settings.PLAYERDATA;

public class Stats extends PaintballCommand {

    public void onCommand(Player player, String[] args) {
        String targetName = args.length == 1 ? player.getName() : args[1];

        PLAYERDATA.getStats(player, targetName);
    }

    public String getName() {
        return "stats";
    }

    public String getInfo() {
        return "View player's statistics (kills,deaths,etc)";
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
