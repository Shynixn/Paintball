package me.synapz.paintball.commands.admin;

import me.synapz.paintball.commands.StatCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.ChatColor;

public class SetHolo extends StatCommand {

    @Override
    public void onCommand() {
        int page = 1;
        int max = Settings.getSettings().getStatsFolder().getMaxPage();

        if (!Settings.HOLOGRAPHIC_DISPLAYS) {
            Messenger.error(player, Messages.DOWNLOAD_HOLO, Messages.HOLO_LINK);
            return;
        }

        if (args.length == 4) {
            if (args[3].contains(",") || args[3].contains(".")) {
                Messenger.error(player, Messages.PAGE_REAL_NUMBER);
                return;
            }

            try {
                page = Integer.parseInt(args[3]);
            } catch (NumberFormatException exc) {
                Messenger.error(player, Messages.PAGE_REAL_NUMBER);
                return;
            }
        }

        if (page <= 0) {
            Messenger.error(player, Messages.PAGE_BIGGER);
            return;
        } else if (page > max) {
            Messenger.error(player, "Page " + ChatColor.GRAY + page + ChatColor.RED + "/" + ChatColor.GRAY + max + ChatColor.RED + " cannot be found.");
            return;
        }

        Settings.ARENA.addLeaderboard(player.getLocation().add(0, 4.5, 0), type, page, true);
        Messenger.success(player, Messages.HOLO_SET);
    }

    @Override
    public String getName() {
        return "setholo";
    }

    @Override
    public Messages getInfo() {
        return Messages.COMMAND_SETHOLO_INFO;
    }

    @Override
    public String getArgs() {
        return "<stat/all> [page]";
    }

    @Override
    public String getPermission() {
        return "paintball.admin.setholo";
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    @Override
    public int getMaxArgs() {
        return 4;
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    protected int getStatArg() {
        return 2;
    }
}
