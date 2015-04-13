package me.synapz.paint.commands;


import me.synapz.paint.Message;
import me.synapz.paint.commands.admin.*;
import me.synapz.paint.commands.player.Join;
import me.synapz.paint.commands.player.LeaveArena;
import me.synapz.paint.commands.player.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager implements CommandExecutor{


    private static ArrayList<Command> commands = new ArrayList<Command>();

    private static final String NO_CONSOLE_PERMS = "Console does not have access to that command!";
    private static final String COMMAND_NOT_FOUND = "Unknown Command! Type /paintball for a list of commands.";
    private static final String HELP_TITLE = ChatColor.DARK_GRAY + "*********************" + ChatColor.GRAY + "" + ChatColor.BOLD + "{- " + Message.SUFFIX + ChatColor.GRAY + "" + ChatColor.BOLD + " -}" + ChatColor.DARK_GRAY + "*********************";


    public void init() {
        // player menu
        commands.add(new Join());
        commands.add(new LeaveArena());
        commands.add(new List());
        commands.add(new Admin(Command.CommandType.PLAYER));

        // admin menu
        commands.add(new CreateArena());
        commands.add(new SetLobbySpawn());
        commands.add(new SetSpawn());
        commands.add(new SetMin());
        commands.add(new SetMax());
        commands.add(new Admin(Command.CommandType.ADMIN)); // used so it gets displayed in /paintball admin
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
                Command command = stringToCommand(args[0], player);

                if (nullCheck(command, player)) {
                    return true;
                }

                if (command.getName().equalsIgnoreCase("admin")) {
                    if (args.length == 1) {
                        dispatchCommand(command, player, args);
                    }
                    else {
                        Command command1 = stringToCommand(args[1], player);
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

    private Command stringToCommand(String commandString, Player player) {
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

        String beginning = type == Command.CommandType.ADMIN ? ChatColor.DARK_AQUA + "/paintball " + "admin " : ChatColor.DARK_AQUA + "/paintball ";
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

            int[] handledArgs = command.getHandledArgs();
            if (command.getName().equals("join") && handledArgs[0] != args.length && handledArgs[1] != args.length) {
                Message.getMessenger().msg(player, ChatColor.RED, "Please use the correct argument count", "Usage: " + command.getCorrectUsage(command));
                return;
            }

            int length = command.getHandledArgs()[0];
            if (args.length != length && !command.getName().equals("join")) {
                String error = args.length < length ? "Not enough arguments!" : "To many arguments!";
                Message.getMessenger().msg(player, ChatColor.RED, error, "Usage: " + command.getCorrectUsage(command));
                return;
            }
            command.onCommand(player, args);
        }catch (Exception e) {
            Message.getMessenger().msg(player, ChatColor.RED, "An internal error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
