package me.synapz.paintball.commands;

import me.synapz.paintball.commands.admin.*;
import me.synapz.paintball.commands.arena.*;
import me.synapz.paintball.commands.player.*;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static me.synapz.paintball.storage.Settings.SECONDARY;
import static me.synapz.paintball.storage.Settings.THEME;
import static org.bukkit.ChatColor.WHITE;

public class CommandManager implements CommandExecutor{

    private static Map<String, PaintballCommand> COMMANDS = new HashMap<String, PaintballCommand>();

    public void init() {
        addCommands(new Join(), new Leave(), new Spectate(), new Stats(), new List(), new Admin(CommandType.PLAYER),
                new Create(), new Remove(), new SetLocation(), new DelLocation(), new SetSpectate(), new DelSpectate(), new SetFlag(), new DelFlag(), new SetMin(),
                new SetMax(), new SetTeams(), new Start(), new Stop(), new Rename(), new Convert(), new Enable(), new Disable(), new SetHolo(), new DelHolo(),
                new Steps(), new Info(), new Reload(), new Reset(), new Top(), new Admin(CommandType.ADMIN), new Arena(CommandType.ARENA));
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {

        if (cmd.getName().equalsIgnoreCase("paintball")) {
            if (!(sender instanceof Player)) {
                Messenger.error(sender, Messages.NO_CONSOLE_PERMISSION);
                return true;
            }

            Player player = (Player) sender;

            if (args.length == 0) {
                displayHelp(player, CommandType.PLAYER);
                return true;
            }

            else if (args.length >= 1) {
                PaintballCommand command = COMMANDS.get(args[0].toLowerCase());

                if (nullCheck(command, player)) {
                    return true;
                }

                if (command.getName().equalsIgnoreCase("admin") || command.getName().equalsIgnoreCase("arena")) {
                    if (args.length == 1) {
                        dispatchCommand(command, player, args);
                    } else {
                        PaintballCommand command1 = COMMANDS.get(args[1].toLowerCase());

                        if (nullCheck(command1, player)) {
                            return true;
                        }
                        if (command1.getCommandType() == command.getCommandType())
                            dispatchCommand(command1, player, args);
                        else
                            Messenger.error(sender, "Wrong command type.", "Did you mean " + command1.getCorrectUsage().replace("Usage: ", ""));
                    }
                    return true;
                }
                dispatchCommand(command, player, args);
            }
        }
        return false;
    }

    private boolean nullCheck(PaintballCommand command, CommandSender sender) {
        try{
            command.getName();
            return false;
        }catch(Exception e) {
            Messenger.error(sender, Messages.INVALID_COMMAND);
            return true;
        }
    }

    public static void displayHelp(Player player, CommandType type) {
        boolean isPlayerType = type == CommandType.PLAYER;
        boolean isArenatype = type == CommandType.ARENA;
        player.sendMessage(Messenger.getHelpTitle(type));

        String beginning = isPlayerType ? THEME + "/pb ": isArenatype ? THEME + "/pb arena " : THEME + "/pb admin ";
        for (PaintballCommand command : COMMANDS.values()) {
            String args = command.getArgs().equals("") ? "" : " " + command.getArgs();
            if (command.getCommandType() == type || command.getName().equals("admin") && player.hasPermission("paintball.admin.help") || command.getName().equals("arena") && player.hasPermission("paintball.arena.help")) {
                player.sendMessage((command.getName().equals("arena") && type == CommandType.ADMIN ? THEME + "/pb arena" : command.getName().equals("admin") && type == CommandType.ARENA ? THEME + "/pb admin" : beginning) + ((command.getName().equals("admin") || command.getName().equals("arena")) && type != CommandType.PLAYER ? "" : command.getName()) + args + WHITE + " - " + SECONDARY + command.getInfo().getString());

            }
        }
    }

    // Sends out the command if: The player has the permission, correct arguments, and fails if there is an exception then sends the player the error
    private void dispatchCommand(PaintballCommand command, Player player, String[] args) {
        try {
            if (!Messenger.permissionValidator(player, command.getPermission())) {
                return;
            }
            if (argumentChecker(command, player, args)) {
                command.onCommand(player, args);
            }
        } catch (Exception e) {
            Messenger.error(player, Messages.INTERNAL_ERROR);
            e.printStackTrace();
        }
    }

    // Checks arguments of a command
    private boolean argumentChecker(PaintballCommand command, Player player, String[] args) {
        if (command.getMaxArgs() == command.getMinArgs()) {
            if (args.length < command.getMinArgs()) {
                Messenger.wrongUsage(command, player, Messenger.Usage.NOT_ENOUGH_ARGS);
                return false;
            } else if (args.length > command.getMaxArgs()) {
                Messenger.wrongUsage(command, player, Messenger.Usage.TO_MANY_ARGS);
                return false;
            }
        } else {
            if (args.length < command.getMinArgs()) {
                Messenger.wrongUsage(command, player, Messenger.Usage.NOT_ENOUGH_ARGS);
                return false;
            } else if (args.length > command.getMaxArgs()) {
                Messenger.wrongUsage(command, player, Messenger.Usage.TO_MANY_ARGS);
                return false;
            }
        }
        return true;
    }

    // Add a list of commands to the COMMANDS list
    private void addCommands(PaintballCommand...cmds) {
        for (PaintballCommand cmd : cmds) {
            COMMANDS.put(cmd.getName(), cmd);
        }
    }
}