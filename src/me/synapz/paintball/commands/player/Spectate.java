package me.synapz.paintball.commands.player;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Message;
import me.synapz.paintball.Utils;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Spectate extends Command{

    public void onCommand(Player player, String[] args) {
        Arena arena = ArenaManager.getArenaManager().getArena(args[1]);

        if (Utils.nullCheck(args[1], arena, player)) {
            switch (arena.getState()) {
                case NOT_SETUP:
                    Message.getMessenger().msg(player, ChatColor.RED, arena.toString() + ChatColor.RED + " has not been fully setup.");
                    return;
                case DISABLED:
                    Message.getMessenger().msg(player, ChatColor.RED, arena.toString() + ChatColor.RED + " is disabled.");
                    return;
                default:
                    break;
            }
            Message.getMessenger().msg(player, ChatColor.GREEN, "Joining " + arena.toString() + " spectate zone.");
            arena.joinSpectate(player);
        }
    }

    public String getName() {
        return "spectate";
    }

    public String getInfo() {
        return "Spectate an arena.";
    }

    public String getArgs() {
        return "<arena>";
    }

    public String getPermission() {
        return "panintball.spectate";
    }

    public CommandType getCommandType() {
        return CommandType.PLAYER;
    }

    public int getMaxArgs() {
        return 2;
    }

    public int getMinArgs() {
        return 2;
    }
}
