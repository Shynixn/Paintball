package me.synapz.paintball.commands.arena;


import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Message;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CreateArena extends Command {

    public void onCommand(Player player, String[] args) {
        String arenaName = args[2];

        try {
            Arena a = ArenaManager.getArenaManager().getArena(arenaName);
            a.getName(); // test to see if the arena exists by getting its name
            Message.getMessenger().msg(player, false, ChatColor.RED, "An arena named " + arenaName + " already exists!");
            return;
        } catch (NullPointerException e) {
            // Arena doesn't except in the list of arenas therefore you can make it...
        }

        Arena a = new Arena(arenaName, arenaName);
        Message.getMessenger().msg(player, false, ChatColor.GREEN, a.toString() + " successfully created!");
        ArenaManager.getArenaManager().addNewArenaToConfig(a);
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
