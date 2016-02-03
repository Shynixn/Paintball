package me.synapz.paintball.commands.admin;


import me.synapz.paintball.Message;
import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Utils;
import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Disable extends ArenaCommand {

    public void onCommand() {
        if (!args[1].equalsIgnoreCase("disable")) {
            Message.getMessenger().msg(player, false, ChatColor.RED, args[1] + " is an invalid choice. Use either enable/disable");
            return;
        }

        if (Utils.nullCheck(args[2], arena, player)) {
            if (!arena.isSetup()) {
                Message.getMessenger().msg(player, false, ChatColor.RED, arena.toString() + ChatColor.RED + " has not been setup.");
                return;
            }
            if (!arena.isEnabled()) {
                Message.getMessenger().msg(player, false, ChatColor.RED, arena.toString() + ChatColor.RED + " is already disable.");
                return;
            }
            Message.getMessenger().msg(player, false, ChatColor.GREEN, arena.toString() + " has been disabled!");
            arena.setEnabled(false);
        }
    }

    public String getArgs() {
        String args = "<arena>";
        return args;
    }

    public String getPermission() {
        return "paintball.admin.disable";
    }

    public String getName() {
        return "disable";
    }

    public String getInfo() {
        return "Disable an Arena";
    }

    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    public int getMaxArgs() {
        return 3;
    }

    public int getMinArgs() {
        return 3;
    }

    protected int getArenaArg() {
        return 2;
    }
}
