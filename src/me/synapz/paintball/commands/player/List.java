package me.synapz.paintball.commands.player;


import me.synapz.paintball.Message;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class List extends Command{

    public void onCommand(Player player, String[] args) {
        ArenaManager.getArenaManager().getList(player);
    }

    public String getName() {
        return "list";
    }

    public String getInfo() {
        return "Get all the Paintball arenas";
    }

    public String getArgs() {
        return "";
    }

    public String getPermission() {
        return "paintball.list";
    }

    public CommandType getCommandType() {
        return CommandType.PLAYER;
    }

    public int getMaxArgs() {
        return 1;
    }

    public int getMinArgs() {
        return 1;
    }

}
