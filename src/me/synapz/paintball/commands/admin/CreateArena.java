package me.synapz.paintball.commands.admin;


import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.Message;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CreateArena extends Command{

    public void onCommand(Player player, String[] args) {
        String arenaName = args[2];

        if (ArenaManager.getArenaManager().getArenas().contains(new Arena(arenaName))) {
            Message.getMessenger().msg(player, ChatColor.RED, "An arena named " + arenaName + " already exists!");
        }

        Arena a = new Arena(arenaName);

        Message.getMessenger().msg(player, ChatColor.GREEN, "Arena " + a.getName() + " successfully created.", a.getSteps());
        ArenaManager.getArenaManager().addNewArenaToConfig(a);
    }

    public String getName() {
        return "create";
    }

    public String getInfo() {
        return "Create a new Paintball Arena";
    }

    public String getArgs() {
        return "<name>";
    }

    public String getPermission() {
        return "paintball.admin.create";
    }

    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    public int getMaxArgs() {
        return 3;
    }

    public int getMinArgs() {
        return 3;
    }

}
