package me.synapz.paintball.commands.arena;

import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;

import static org.bukkit.ChatColor.*;

public class SetMax extends ArenaCommand {

    public void onCommand() {
        String maxString = args[3];
        int max;

        try {
            max = Integer.parseInt(maxString);
        } catch (NumberFormatException e) {
            Messenger.error(player, maxString + " is not a valid number!");
            return;
        }

        if (arena.getMin() == 0 || max > arena.getMin()) {
            if (max <= 0) {
                Messenger.error(player, "Max must be greater than 0!");
                return;
            }
            arena.setMaxPlayers(max);
        } else {
            Messenger.error(player, "Your max (" + GRAY + max + RED + ") must be greater than your min (" + GRAY + arena.getMin() + RED + ")!");
            return;
        }

        if (max > arena.getArenaTeamList().size() && arena.getAllArenaPlayers().size() != 0) {
            Messenger.error(player, "Max (" + GRAY + max + RED + ") must be greater than the number of teams (" + GRAY + arena.getArenaTeamList().size() + RED + ")!");
            return;
        }
        Messenger.success(player, "Max players for " + arena.toString(GREEN) + " set to " + GRAY + max, arena.getSteps());
    }

    public String getName() {
        return "max";
    }

    public String getInfo() {
        return "Set max number of players";
    }

    public String getArgs() {
        return "<arena> <number>";
    }

    public String getPermission() {
        return "paintball.arena.max";
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
