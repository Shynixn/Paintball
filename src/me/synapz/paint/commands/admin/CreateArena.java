package me.synapz.paint.commands.admin;


import me.synapz.paint.arenas.Arena;
import me.synapz.paint.arenas.ArenaManager;
import me.synapz.paint.Message;
import me.synapz.paint.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CreateArena extends Command{

    public void onCommand(Player player, String[] args) {
        String arenaName = args[2];

        if (ArenaManager.getArenaManager().getArenas().contains(ArenaManager.getArenaManager().getArena(arenaName))) {
            Message.getMessenger().msg(player, ChatColor.RED, "Arena " + arenaName + " already exists!");
        }

        Arena a = new Arena(arenaName);
        ArenaManager.getArenaManager().addArena(a);

        Message.getMessenger().msg(player, ChatColor.GREEN, "Arena " + a.getName() + " successfully created.", "Next steps: " + getNextSteps());
    }

    public String[] getNextSteps() {
        String[] steps = {"setlobby", "setspawn"};
        return steps;
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

    public int getArgsInt() {
        return 3;
    }

}
