package me.synapz.paintball.commands.admin;


import me.synapz.paintball.Message;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RemoveArena extends Command{

    public void onCommand(Player player, String[] args) {
        String arenaName = args[2];
        Arena arena = ArenaManager.getArenaManager().getArena(arenaName);

        if (nullCheck(arenaName, arena, player)) {
            arena.removeArena();
            Message.getMessenger().msg(player, ChatColor.GREEN, arena.toString() + " successfully removed!");
        }
    }

    public String getName() {
        return "remove";
    }

    public String getInfo() {
        return "Remove an arena";
    }

    public String getArgs() {
        return "<arena>";
    }

    public String getPermission() {
        return "paintball.admin.remove";
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
