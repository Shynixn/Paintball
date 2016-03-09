package me.synapz.paintball.commands.player;

import me.synapz.paintball.Messenger;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;
import static me.synapz.paintball.storage.Settings.*;

import java.util.Map;
import java.util.UUID;

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
