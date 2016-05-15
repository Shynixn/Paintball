package me.synapz.paintball.commands.arena;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.ArenaCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.ChatColor;

public class Rename extends ArenaCommand {

	 public void onCommand() {
		 String newName = args[3];
		 Arena newArena = ArenaManager.getArenaManager().getArena(args[3]);

		 if (newArena != null) {
			 Messenger.error(player, new MessageBuilder(Messages.ARENA_NAME_EXISTS).replace(Tag.ARENA, newName).build());
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

	public Messages getInfo() {
	        return Messages.COMMAND_RENAME_INFO;
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
