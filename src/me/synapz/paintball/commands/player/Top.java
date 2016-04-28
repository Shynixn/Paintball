package me.synapz.paintball.commands.player;


import me.synapz.paintball.commands.StatCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Messenger;
import net.md_5.bungee.api.ChatColor;

public class Top extends StatCommand {

    public void onCommand() {
        int page = 1;
        int maxPage = Settings.PLAYERDATA.getMaxPage();

        if (args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (NumberFormatException exc) {
                Messenger.error(player, "Please specify a real number as the page.");
                return;
            }

            if (page <= 0) {
                Messenger.error(player, "The page cannot be lower than 0");
                return;
            } else if (page > maxPage) {
                Messenger.error(player, "Page " + ChatColor.GRAY + page + ChatColor.RED + "/" + ChatColor.GRAY + maxPage + ChatColor.RED + " cannot be found.");
                return;
            }
        }

        for (String statLine : Settings.PLAYERDATA.getPage(type, page)) {
            Messenger.msg(player, statLine);
        }
    }

    public String getArgs() {
        String args = "<stat/all> [page]";
        return args;
    }

    public String getPermission() {
        return "paintball.top";
    }

    public String getName() {
        return "top";
    }

    public Messages getInfo() {
        return Messages.COMMAND_TOP_INFO;
    }

    public CommandType getCommandType() {
        return CommandType.PLAYER;
    }

    public int getMaxArgs() {
        return 3;
    }

    public int getMinArgs() {
        return 2;
    }

    protected int getStatArg() {
        return 1;
    }
}
