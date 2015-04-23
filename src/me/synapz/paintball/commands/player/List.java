package me.synapz.paintball.commands.player;


import me.synapz.paintball.Message;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class List extends Command{

    public void onCommand(Player player, String[] args) {
        int page;
        int size = ArenaManager.getArenaManager().getArenas().size() / 10;

        if (args.length == 1) {
            page = 1;
        } else {
            try {
                page = Integer.parseInt(args[1]);
            }catch (NumberFormatException e) {
                Message.getMessenger().msg(player, ChatColor.RED, args[1] + " is not an integer.", getCorrectUsage(this));
                return;
            }
        }
        if (size < page && size != 0) {
            Message.getMessenger().msg(player, ChatColor.RED, "Page number is to big, max page is " + size, getCorrectUsage(this));
            return;
        }
        ArenaManager.getArenaManager().getList(player, page);
    }

    public String getName() {
        return "list";
    }

    public String getInfo() {
        return "List of all arenas";
    }

    public String getArgs() {
        return "<page number>";
    }

    public String getPermission() {
        return "paintball.list";
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
