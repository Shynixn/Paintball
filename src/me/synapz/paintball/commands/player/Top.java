package me.synapz.paintball.commands.player;


import me.synapz.paintball.commands.StatCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.ChatColor;

public class Top extends StatCommand {

    public void onCommand() {
        int page = 1;
        int maxPage = Settings.PLAYERDATA.getMaxPage();

        if (args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (NumberFormatException exc) {
                Messenger.error(player, Messages.PAGE_REAL_NUMBER);
                return;
            }

            if (page <= 0) {
                Messenger.error(player, Messages.PAGE_BIGGER);
                return;
            } else if (page > maxPage) {
                Messenger.error(player, new MessageBuilder(Messages.PAGE_FIND_ERROR).replace(Tag.AMOUNT, String.valueOf(ChatColor.GRAY + "" + page + ChatColor.RED)).replace(Tag.MAX, ChatColor.GRAY + "" + maxPage + ChatColor.RED).build());
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
