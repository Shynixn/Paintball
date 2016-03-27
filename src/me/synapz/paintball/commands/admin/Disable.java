package me.synapz.paintball.commands.admin;


import me.synapz.paintball.Messenger;
import me.synapz.paintball.Utils;
import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import org.bukkit.ChatColor;

public class Disable extends ArenaCommand {

    public void onCommand() {
        if (!args[1].equalsIgnoreCase("disable")) {
            Messenger.error(player, args[1] + " is an invalid choice. Use either enable/disable");
            return;
        }

        if (Utils.nullCheck(args[2], arena, player)) {
            if (!arena.isSetup()) {
                Messenger.error(player, arena.toString(ChatColor.RED) + " has not been setup.");
                return;
            }
            if (!arena.isEnabled()) {
                Messenger.error(player, arena.toString(ChatColor.RED) + " is already disabled.");
                return;
            }
            if (!arena.getAllPlayers().containsKey(player))
                Messenger.success(player, arena.toString(ChatColor.GREEN) + " has been disabled!");
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
