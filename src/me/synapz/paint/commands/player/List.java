package me.synapz.paint.commands.player;


import me.synapz.paint.Message;
import me.synapz.paint.arenas.Arena;
import me.synapz.paint.arenas.ArenaManager;
import me.synapz.paint.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class List extends Command{

    public void onCommand(Player player, String[] args) {
        String arenas = "";
        String name = "";
        for (Arena a : ArenaManager.getArenaManager().getArenas()) {
            if (!a.isSetup()) {
                name = ChatColor.STRIKETHROUGH + a.getName() + ChatColor.RESET + ChatColor.GRAY;
            } else {
                name = a.getName();
            }

            arenas = arenas + ", " + name;
        }

        if (arenas.equals("")) {
            arenas = "There are currently no arenas.";
            Message.getMessenger().msg(player, ChatColor.BLUE, arenas);
            return;
        }

        Message.getMessenger().msg(player, ChatColor.BLUE, "Arenas: " + ChatColor.GRAY + arenas.substring(2, arenas.length()));
    }

    public String getName() {
        return "list";
    }

    public String getInfo() {
        return "Get all the Paintball arenas";
    }

    public String getArgs() {
        return "";
    }

    public String getPermission() {
        return "paintball.list";
    }

    public CommandType getCommandType() {
        return CommandType.PLAYER;
    }

    public int getMaxArgs() {
        return 1;
    }

    public int getMinArgs() {
        return 1;
    }

}
