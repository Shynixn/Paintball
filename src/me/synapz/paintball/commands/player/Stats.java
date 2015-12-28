package me.synapz.paintball.commands.player;

import me.synapz.paintball.Message;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.ChatColor.*;
import static me.synapz.paintball.storage.Settings.*;

import java.util.Map;
import java.util.UUID;

public class Stats extends Command {

    // TODO fix stats
    public void onCommand(Player player, String[] args) {
        UUID target = args.length == 1 ? player.getUniqueId() : Bukkit.getPlayer(args[1]) == null ? Bukkit.getOfflinePlayer(args[1]).getUniqueId() : Bukkit.getPlayer(args[1]).getUniqueId();
        // todo: better way of doing this..
        Paintball pb = (Paintball) JavaPlugin.getProvidingPlugin(Paintball.class);
        Map<StatType, String> stats = pb.getPlayerData().getPlayerStats(target);

        Message.getMessenger().msg(player, false, false, SECONDARY + STRIKETHROUGH + "             " + RESET + " " + THEME + Bukkit.getOfflinePlayer(target).getName() + "'s Stats" + RESET + " " + SECONDARY + STRIKETHROUGH + "             ");

        for (StatType type : StatType.values()) {
            String name = type.getName();
            if (type == StatType.SHOTS || type == StatType.HITS || type == StatType.KILLS || type == StatType.DEATHS || type == StatType.DEFEATS || type == StatType.WINS)
                name = "  " + name;
            Message.getMessenger().msg(player, false, false, THEME + name + ": " + SECONDARY + stats.get(type));
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
