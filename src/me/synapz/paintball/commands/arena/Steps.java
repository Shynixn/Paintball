package me.synapz.paintball.commands.arena;


import me.synapz.paintball.Message;
import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Utils;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Steps extends Command{

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);

        if (Utils.nullCheck(args[2], arena, player)) {
            Message.getMessenger().msg(player, false, ChatColor.GRAY, arena.getSteps());
        }
    }

    public String getArgs() {
        String args = "<arena>";
        return args;
    }

    public String getPermission() {
        return "paintball.arena.steps";
    } // gives access to enable + disable

    public String getName() {
        return "steps";
    }

    public String getInfo() {
        return "List steps of an Arena";
    }

    public Command.CommandType getCommandType() {
        return Command.CommandType.ARENA;
    }

    public int getMaxArgs() {
        return 3;
    }

    public int getMinArgs() {
        return 3;
    }
}