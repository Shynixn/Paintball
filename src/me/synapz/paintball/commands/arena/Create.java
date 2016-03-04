package me.synapz.paintball.commands.arena;


import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Create extends PaintballCommand {

    public void onCommand(Player player, String[] args) {
        String arenaName = args[2];
        Arena newArena = ArenaManager.getArenaManager().getArena(arenaName);

        if (newArena != null) {
            Messenger.error(player, "An arena named " + arenaName + " already exists!");
            return;
        } else {
            Arena a = new Arena(arenaName, arenaName, true);
            Messenger.success(player, a.toString() + " successfully created!", a.getSteps());
        }
    }

    public String getName() {
        return "create";
    }

    public String getInfo() {
        return "Create a new Arena";
    }

    public String getArgs() {
        return "<arena>";
    }

    public String getPermission() {
        return "paintball.arena.create";
    }

    public CommandType getCommandType() {
        return CommandType.ARENA;
    }

    public int getMaxArgs() {
        return 3;
    }

    public int getMinArgs() {
        return 3;
    }

}
