package me.synapz.paintball.commands;


import me.synapz.paintball.Message;
import me.synapz.paintball.commands.arena.*;
import me.synapz.paintball.commands.player.*;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.commands.admin.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandManager implements CommandExecutor{


    private static Map<String, Command> commands = new HashMap<String, Command>();

    private static final String NO_CONSOLE_PERMS = "Console does not have access to that command!";
    private static final String COMMAND_NOT_FOUND = "Unknown Command! Type /paintball for a list of commands.";


    public void init() {
    	addCommands(new Join(), new LeaveArena(), new Spectate(), new Stats(), new Leaderboard(), new List(), new Admin(Command.CommandType.PLAYER),
    			new CreateArena(), new RemoveArena(), new SetLobbySpawn(), new SetSpawn(), new SetSpectate(), new SetMin(),
    			new SetMax(), new SetTeams(), new ForceStart(), new ForceStop(), new Rename(), new Enable(), new Disable(),
    			new Steps(),  new Info(), new Reload(), new Admin(Command.CommandType.ADMIN), new Arena(Command.CommandType.ARENA));
    }


    
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {

        if (cmd.getName().equalsIgnoreCase("paintball")) {
            if (!(sender instanceof Player)) {
                Message.getMessenger().msg(sender, false, ChatColor.RED, NO_CONSOLE_PERMS);
                return true;
            }

            Player player = (Player) sender;

            if (args.length == 0) {
                displayHelp(player, Command.CommandType.PLAYER);
                return true;
            }

            else if (args.length >= 1) {
                Command command = commands.get(args[0]);

                if (nullCheck(command, player)) {
                    return true;
                }

                if (command.getName().equalsIgnoreCase("admin") || command.getName().equalsIgnoreCase("arena")) {
                    if (args.length == 1) {
                        dispatchCommand(command, player, args);
                    }
                    else {
                        Command command1 = commands.get(args[1]);
                        if (nullCheck(command1, player)) {
                            return true;
                        }
                        dispatchCommand(command1, player, args);
                    }
                    return true;
                }
                dispatchCommand(command, player, args);
            }
        }
        return false;
    }

    private boolean nullCheck(Command command, CommandSender sender) {
        try{
            command.getName();
            return false;
        }catch(Exception e) {
            Message.getMessenger().msg(sender, false, ChatColor.RED, COMMAND_NOT_FOUND);
            return true;
        }
    }


    // TODO: add command ARENA help
    public static void displayHelp(Player player, Command.CommandType type) {
        boolean isPlayerType = type == Command.CommandType.PLAYER;
        boolean isArenatype = type == Command.CommandType.ARENA;
        player.sendMessage(Message.getMessenger().getHelpTitle(type));

        String beginning = isPlayerType ? Settings.getSettings().getTheme() + "/pb ": isArenatype ? Settings.getSettings().getTheme() + "/pb arena " : Settings.getSettings().getTheme() + "/pb admin ";
        for (Command command : commands.values()) {
            String name = command.getName().equals("admin") && type == Command.CommandType.ADMIN || command.getName().equals("arena") && type == Command.CommandType.ARENA ? "" : command.getName();
            String args = command.getArgs().equals("") ? "" : " " + command.getArgs();
            if (command.getCommandType() == type) {
                player.sendMessage(beginning + name + args + ChatColor.WHITE + " - " + Settings.getSettings().getSecondaryColor() + command.getInfo());
            }
        }
    }

    private void dispatchCommand(Command command, Player player, String[] args) {
        try {
            if (!Message.getMessenger().permissionValidator(player, command.getPermission())) {
                return;
            }
            if (argumentChecker(command, player, args)) {
                command.onCommand(player, args);
            }
        }catch (Exception e) {
            Message.getMessenger().msg(player, false, ChatColor.RED, "An internal error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean argumentChecker(Command command, Player player, String[] args) {
        if (command.getMaxArgs() == command.getMinArgs()) {
            if (args.length < command.getMinArgs()) {
                Message.getMessenger().wrongUsage(command, player, Message.Usage.NOT_ENOUGH_ARGS);
                return false;
            } else if (args.length > command.getMaxArgs()) {
                Message.getMessenger().wrongUsage(command, player, Message.Usage.TO_MANY_ARGS);
                return false;
            }
        } else {
            if (args.length < command.getMinArgs()) {
                Message.getMessenger().wrongUsage(command, player, Message.Usage.NOT_ENOUGH_ARGS);
                return false;
            } else if (args.length > command.getMaxArgs()) {
                Message.getMessenger().wrongUsage(command, player, Message.Usage.TO_MANY_ARGS);
                return false;
            }
        }
        return true;
    }
    
    private void addCommands(Command...cmds) {
    	for (Command cmd : cmds) {
    		commands.put(cmd.getName(), cmd);
    	}
    }
}
