package me.synapz.paintball.commands.player;


import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import org.bukkit.entity.Player;

public class List extends PaintballCommand {

    public void onCommand(Player player, String[] args) {
        ArenaManager.getArenaManager().getList(player);
    }

    public String getName() {
        return "list";
    }

    public String getInfo() {
        return "List of all arenas";
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
