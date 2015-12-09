package me.synapz.paintball.commands.player;


import me.synapz.paintball.Message;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.StatType;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class Leaderboard extends Command {

    public void onCommand(Player player, String[] args) {
        StatType statType = null;
        int page;
        // get stat
        for (StatType type : StatType.values()) {
            if (type.getSignName().equalsIgnoreCase(args[1]))
                statType = type;
        }
        if (statType == null) {
            Message.getMessenger().msg(player, false, ChatColor.RED, this.getCorrectUsage(this), args[1] + " is an invalid statistic type.", "Choose either " + StatType.getReadableList());
            return;
        }

        // get page
        try {
            page = Integer.parseInt(args[2]);
        } catch (NumberFormatException exc) {
            Message.getMessenger().msg(player, false, ChatColor.RED, this.getCorrectUsage(this), "Please specify a real number as the page.");
            return;
        }

        // calculate the page and set it to the player
        // TODO better way of doing this
        Paintball pb = (Paintball) JavaPlugin.getProvidingPlugin(Paintball.class);
        // pb.getPlayerData().getPage(player, statType, page);
        SortedMap<Integer, String> map = new TreeMap<Integer, String>(Collections.reverseOrder());
        map.put(1, "Thing");
        map.put(100, "thing2");
        pb.getPlayerData().paginate(player, statType, 1, 5);
    }

    public String getArgs() {
        String args = "<stat> <page>";
        return args;
    }

    public String getPermission() {
        return "paintball.leaderboard";
    }

    public String getName() {
        return "leaderboard";
    }

    public String getInfo() {
        return "View a stat's leaderboard.";
    }

    public Command.CommandType getCommandType() {
        return Command.CommandType.PLAYER;
    }

    public int getMaxArgs() {
        return 3;
    }

    public int getMinArgs() {
        return 3;
    }
}
