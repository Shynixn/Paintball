package me.synapz.paintball.commands.admin;


import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.ChatColor;

public class Enable extends ArenaCommand {

    public void onCommand() {
        if (!args[1].equalsIgnoreCase("enable")) {
            Messenger.error(player, new MessageBuilder(Messages.CHOOSE_ENABLE_OR_DISABLE).replace(Tag.COMMAND, args[1]).build());
            return;
        }

        if (!arena.isSetup()) {
            Messenger.error(player, new MessageBuilder(Messages.ARENA_NOT_SETUP).replace(Tag.ARENA, arena.toString(ChatColor.RED)).build());
            return;
        }
        if (arena.isEnabled()) {
            Messenger.error(player, new MessageBuilder(Messages.ARENA_ENABLED).replace(Tag.ARENA, arena.toString(ChatColor.RED)).build());
            return;
        }
        Messenger.success(player, new MessageBuilder(Messages.ENABLE_SUCCESS).replace(Tag.ARENA, arena.toString(ChatColor.GREEN)).build());
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
