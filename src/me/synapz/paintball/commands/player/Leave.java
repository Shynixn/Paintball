package me.synapz.paintball.commands.player;


import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Leave extends PaintballCommand {

    public void onCommand(Player player, String[] args) {
        Arena a;

        try {
            a = ArenaManager.getArenaManager().getArena(player);
            a.getName(); // used to see if it returns null
        }catch (NullPointerException e) {
            Messenger.error(player, "You are not in an arena.");
            return;
        }

        a.getAllPlayers().get(player).leave();
        Messenger.success(player, "Successfully left arena.");
    }

    public String getArgs() {
        String args = "";
        return args;
    }

    public String getPermission() {
        return "paintball.leave";
    }

    public String getName() {
        return "leave";
    }

    public String getInfo() {
        return "Leave an Arena";
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
