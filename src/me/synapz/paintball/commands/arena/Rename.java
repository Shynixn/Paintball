package me.synapz.paintball.commands.arena;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import org.bukkit.ChatColor;

public class Rename extends ArenaCommand {

	 public void onCommand() {
		 String newName = args[3];
		 Arena newArena = ArenaManager.getArenaManager().getArena(args[3]);

		 if (newArena != null) {
			 Messenger.error(player, "An arena named " + newName + " already exists!");
			 return;
		 } else {
			 arena.rename(newName);
			 Messenger.success(player, "Successfully renamed Arena " + ChatColor.GRAY + args[2] + ChatColor.GREEN + " to " + ChatColor.GRAY + newName);
		 }
	 }

	public String getArgs() {
		String args = "<arena> <newName>";
		return args;
	}

	public String getPermission() {
	        return "paintball.arena.rename";
	    }

	public String getName() {
	        return "rename";
	    }

	public String getInfo() {
	        return "Rename an Arena";
	    }

	public CommandType getCommandType() {
	        return CommandType.ARENA;
	    }

	public int getMaxArgs() {
	        return 4;
	    }

	public int getMinArgs() {
	        return 4;
	    }

	protected int getArenaArg() {
		return 2;
	}
}
