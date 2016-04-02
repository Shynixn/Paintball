package me.synapz.paintball.commands.admin;

import me.synapz.paintball.Messenger;
import me.synapz.paintball.commands.StatCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public class Reset extends StatCommand {

    public void onCommand() {
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);

        if (type == null) {
            for (StatType type : StatType.values())
                Settings.PLAYERDATA.resetStats(type, target);
        } else {
            Settings.PLAYERDATA.resetStats(type, target);
        }

        String strType = type == null ? "stats" : type.getName() + " stat";

        Messenger.success(player, "Player " + ChatColor.GRAY + target.getName() + ChatColor.GREEN + "'s " + strType + " have been reset.");
    }

    public String getName() {
        return "reset";
    }

    public String getInfo() {
        return "Reset a player's stats";
    }

    public String getArgs() {
        return "<player> [stat]";
    }

    public String getPermission() {
        return "paintball.admin.reset";
    }

    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    public int getMaxArgs() {
        return 4;
    }

    public int getMinArgs() {
        return 3;
    }

    @Override
    protected int getStatArg() {
        return 3;
    }
}