package me.synapz.paintball.commands.admin;

import me.synapz.paintball.Message;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.Command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Rename extends Command{
	 public void onCommand(Player player, String[] args) {
	        Arena arena = ArenaManager.getArenaManager().getArena(args[2]);
	        String newName = args[3];


	        
	        if (nullCheck(args[2], arena, player)) {
	        	
		        try {
		            Arena a = ArenaManager.getArenaManager().getArena(args[3]);
		            Message.getMessenger().msg(player, ChatColor.RED, "An arena named " + newName + " already exists!");
		            return;
		        }catch (NullPointerException e) {
		            // Arena doesn't exist in the list of arenas therefore you can make it...
		        }
		        
	            arena.rename(newName);
	            Message.getMessenger().msg(player, ChatColor.GREEN, "Successfully renamed " + arena.getName() + " to " + newName);
	        }
	    }

	    public String getArgs() {
	        String args = "<arena> <newName>";
	        return args;
	    }

	    public String getPermission() {
	        return "paintball.admin.rename";
	    }

	    public String getName() {
	        return "rename";
	    }

	    public String getInfo() {
	        return "Rename an Arena";
	    }

	    public Command.CommandType getCommandType() {
	        return Command.CommandType.ADMIN;
	    }

	    public int getMaxArgs() {
	        return 4;
	    }

	    public int getMinArgs() {
	        return 4;
	    }
}
