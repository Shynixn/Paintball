package me.synapz.paintball.commands.admin;


import me.synapz.paintball.Message;
import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Utils;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Enable extends Command{

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);

        if (!args[1].equalsIgnoreCase("enable")) {
            Message.getMessenger().msg(player, false, ChatColor.RED, args[1] + " is an invalid choice. Use either enable/disable");
            return;
        }

        if (Utils.nullCheck(args[2], arena, player)) {
            if (!arena.isSetup()) {
                Message.getMessenger().msg(player, false, ChatColor.RED, arena.toString() + ChatColor.RED + " has not been setup.");
                return;
            }
            if (arena.isEnabled()) {
                Message.getMessenger().msg(player, false, ChatColor.RED, arena.toString() + ChatColor.RED + " is already enabled.");
                return;
            }
            Message.getMessenger().msg(player, false, ChatColor.GREEN, arena.toString() + " has been enabled!");
            arena.setEnabled(true);
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
