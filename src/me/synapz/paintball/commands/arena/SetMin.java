package me.synapz.paintball.commands.arena;

import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.ChatColor;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;

public class SetMin extends ArenaCommand {

    public void onCommand() {
        String minString = args[3];
        int min;

        try {
            min = Integer.parseInt(minString);
        } catch (NumberFormatException e) {
            Messenger.error(player, minString + " is not a valid number!");
            return;
        }

        if (arena.getMax() == 0 || min < arena.getMax()) {
            if (min <= 1) {
                Messenger.error(player, "Min must be greater than 1!");
                return;
            }
            arena.setMinPlayers(min);
        } else {
            Messenger.error(player, "Your min (" + GRAY + min + RED + ") must be less than than your max (" + GRAY + arena.getMax() + RED + ") !");
            return;
        }
        Messenger.success(player, "Min players for " + arena.toString(ChatColor.GREEN) + " set to " + ChatColor.GRAY + min, arena.getSteps());
    }

    public String getName() {
        return "min";
    }

    public Messages getInfo() {
        return Messages.COMMAND_SETMIN_INFO;
    }

    public String getArgs() {
        return "<arena> <number>";
    }

    public String getPermission() {
        return "paintball.arena.min";
    }

    public CommandType getCommandType() {
        return CommandType.ARENA;
    }

    public int getMaxArgs() {
        return 4;
    }

    public int getMinArgs() {
        return 4;
    }

    protected int getArenaArg() {
        return 2;
    }
}
