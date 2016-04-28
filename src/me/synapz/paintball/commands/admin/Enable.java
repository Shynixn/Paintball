package me.synapz.paintball.commands.admin;


import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.ChatColor;

public class Enable extends ArenaCommand {

    public void onCommand() {
        if (!args[1].equalsIgnoreCase("enable")) {
            Messenger.error(player, args[1] + " is an invalid choice. Use either enable/disable");
            return;
        }

        if (!arena.isSetup()) {
            Messenger.error(player, arena.toString(ChatColor.RED) + " has not been setup.");
            return;
        }
        if (arena.isEnabled()) {
            Messenger.error(player, arena.toString(ChatColor.RED) + " is already enabled.");
            return;
        }
        Messenger.success(player, arena.toString(ChatColor.GREEN) + " has been enabled!");
        arena.setEnabled(true);
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

    public Messages getInfo() {
        return Messages.COMMAND_ENABLE_INFO;
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
