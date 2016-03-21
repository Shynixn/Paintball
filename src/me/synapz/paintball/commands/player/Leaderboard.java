package me.synapz.paintball.commands.player;


import me.synapz.paintball.Messenger;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.storage.Settings;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class Leaderboard extends PaintballCommand {

    // TODO: Re-Implement to work.

    public void onCommand(Player player, String[] args) {
        StatType statType = null;
        int page;
        // get stat
        for (StatType type : StatType.values()) {
            if (type.getSignName().equalsIgnoreCase(args[1]))
                statType = type;
        }
        if (statType == null) {
            Messenger.error(player, this.getCorrectUsage(), args[1] + " is an invalid statistic type.", "Choose either " + StatType.getReadableList());
            return;
        }

        // get page
        try {
            page = Integer.parseInt(args[2]);
        } catch (NumberFormatException exc) {
            Messenger.error(player, this.getCorrectUsage(), "Please specify a real number as the page.");
            return;
        }

        // calculate the page and set it to the player
        // pb.getPlayerData().getPage(player, statType, page);
        SortedMap<Integer, String> map = new TreeMap<Integer, String>(Collections.reverseOrder());
        map.put(1, "Thing");
        map.put(100, "thing2");
        Settings.PLAYERDATA.paginate(player, statType, 1, 5);
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

    public CommandType getCommandType() {
        return CommandType.PLAYER;
    }

    public int getMaxArgs() {
        return 3;
    }

    public int getMinArgs() {
        return 3;
    }
}
