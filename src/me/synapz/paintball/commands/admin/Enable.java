package me.synapz.paintball.commands.admin;


import me.synapz.paintball.Message;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Enable extends Command{

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);

        if (!args[1].equalsIgnoreCase("enable")) {
            Message.getMessenger().msg(player, ChatColor.RED, args[1] + " is an invalid choice. Use either enable/disable");
            return;
        }

        if (nullCheck(args[2], arena, player)) {
            arena.setEnabled(true, player);
        }
    }

    public String getArgs() {
        String args = "<arena>";
        return args;
    }

    public String getPermission() {
        return "paintball.admin.enable";
    } // gives access to enable + disable

    public String getName() {
        return "enable";
    }

    public String getInfo() {
        return "Enable an Arena";
    }

    public Command.CommandType getCommandType() {
        return Command.CommandType.ADMIN;
    }

    public int getMaxArgs() {
        return 3;
    }

    public int getMinArgs() {
        return 3;
    }
}
