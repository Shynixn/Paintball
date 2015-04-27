package me.synapz.paintball.commands;


import me.synapz.paintball.Message;
import me.synapz.paintball.commands.admin.*;
import me.synapz.paintball.commands.player.Join;
import me.synapz.paintball.commands.player.LeaveArena;
import me.synapz.paintball.commands.player.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CommandManager implements CommandExecutor{


    private static ArrayList<Command> commands = new ArrayList<Command>();

    private static final String NO_CONSOLE_PERMS = "Console does not have access to that command!";
    private static final String COMMAND_NOT_FOUND = "Unknown Command! Type /paintball for a list of commands.";
    private static final String HELP_TITLE = ChatColor.DARK_GRAY + "*********************" + ChatColor.GRAY + "" + ChatColor.BOLD + "{- " + ChatColor.DARK_GRAY + "[" + Message.THEME + "Paintball" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "" + ChatColor.BOLD + " -}" + ChatColor.DARK_GRAY + "*********************";


    public void init() {
    	addCommands(new Join(), new LeaveArena(), new List(), new Admin(Command.CommandType.PLAYER),
    			new CreateArena(), new RemoveArena(), new SetLobbySpawn(), new SetSpawn(), new SetMin(),
    			new SetMax(), new ForceStart(), new ForceStop(), new Rename(), new Enable(), new Disable(),
    			new Steps(), new Reload(), new Admin(Command.CommandType.ADMIN));
    }


    
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {

        if (cmd.getName().equalsIgnoreCase("paintball")) {
            if (!(sender instanceof Player)) {
                Message.getMessenger().msg(sender, ChatColor.RED, NO_CONSOLE_PERMS);
                return true;
            }

            Player player = (Player) sender;

            if (args.length == 0) {
                displayHelp(player, Command.CommandType.PLAYER);
                return true;
            }

            else if (args.length >= 1) {
                Command command = stringToCommand(args[0]);

                if (nullCheck(command, player)) {
                    return true;
                }

                if (command.getName().equalsIgnoreCase("admin")) {
                    if (args.length == 1) {
                        dispatchCommand(command, player, args);
                    }
                    else {
                        Command command1 = stringToCommand(args[1]);
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

    private Command stringToCommand(String commandString) {
        Command command = null;
        for (Command cmd : commands) {
            if (cmd.getName().equalsIgnoreCase(commandString))
                command = cmd;
        }
        return command;
    }

    private boolean nullCheck(Command command, CommandSender sender) {
        try{
            command.getName();
            return false;
        }catch(Exception e) {
            Message.getMessenger().msg(sender, ChatColor.RED, COMMAND_NOT_FOUND);
            return true;
        }
    }


    public static void displayHelp(Player player, Command.CommandType type) {
        player.sendMessage(HELP_TITLE);

        String beginning = type == Command.CommandType.ADMIN ? Message.THEME + "/pb " + "admin " : Message.THEME + "/pb ";
        for (Command command : commands) {
            String name = command.getName().equals("admin") && type == Command.CommandType.ADMIN ? "" : command.getName();
            String args = command.getArgs().equals("") ? "" : " " + command.getArgs();
            if (command.getCommandType() == type) {
                player.sendMessage(beginning + name + args + ChatColor.WHITE + " - " + ChatColor.GRAY + command.getInfo());
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
            Message.getMessenger().msg(player, ChatColor.RED, "An internal error occurred: " + e.getMessage());
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
    		commands.add(cmd);
    	}
    }
}
