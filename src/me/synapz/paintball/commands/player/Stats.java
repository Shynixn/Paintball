package me.synapz.paintball.commands.player;

import me.synapz.paintball.Message;
import me.synapz.paintball.StatType;
import me.synapz.paintball.commands.Command;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.storage.Statistics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import static org.bukkit.ChatColor.*;

import java.util.Map;
import java.util.UUID;

public class Stats extends Command {

    // TODO fix stats
    public void onCommand(Player player, String[] args) {
        UUID target = args.length == 1 ? player.getUniqueId() : Bukkit.getPlayer(args[1]) == null ? Bukkit.getOfflinePlayer(args[1]).getUniqueId() : Bukkit.getPlayer(args[1]).getUniqueId();
        Map<StatType, String> stats = Statistics.instance.getPlayerStats(target);

        String theme = Settings.getSettings().getTheme();
        String sec = Settings.getSettings().getSecondaryColor();
        Message.getMessenger().msg(player, false, false, sec + STRIKETHROUGH + "        " + RESET + " " + theme + Bukkit.getOfflinePlayer(target).getName() + "'s Stats" + RESET + " " + sec + STRIKETHROUGH + "        ");

        for (StatType type : stats.keySet()) {
            Message.getMessenger().msg(player, false, false, Settings.getSettings().getTheme() + type.getName() + ": " + Settings.getSettings().getSecondaryColor() + stats.get(type));
        }
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

    public Command.CommandType getCommandType() {
        return Command.CommandType.PLAYER;
    }

    public int getMaxArgs() {
        return 2;
    }

    public int getMinArgs() {
        return 1;
    }
}
