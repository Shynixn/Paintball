package me.synapz.paintball.commands.player;


import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import org.bukkit.entity.Player;

public class List extends PaintballCommand {

    public void onCommand(Player player, String[] args) {
        ArenaManager.getArenaManager().getList(player);
    }

    public String getName() {
        return "list";
    }

    public Messages getInfo() {
        return Messages.COMMAND_LIST_INFO;
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
