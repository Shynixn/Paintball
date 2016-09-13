package me.synapz.paintball.commands.arena;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.*;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Create extends PaintballCommand {

    public void onCommand(Player player, String[] args) {
        String arenaName = args[2];
        Arena newArena = ArenaManager.getArenaManager().getArena(arenaName);
        ArenaType type = ArenaType.getArenaType(player, args[3]);

        if (type == null)
            return;

        if (newArena != null) {
            Messenger.error(player, new MessageBuilder(Messages.ARENA_NAME_EXISTS).replace(Tag.ARENA, arenaName).build());
            return;
        } else {
            Arena a;

            switch (type) {
                case CTF:
                    a = new CTFArena(arenaName, arenaName, true);
                    break;
                case DOM:
                    a = new DOMArena(arenaName, arenaName, true);
                    break;
                case DTC:
                    a = new DTCArena(arenaName, arenaName, true);
                    break;
                case RTF:
                    a = new RTFArena(arenaName, arenaName, true);
                    break;
                case TDM:
                    a = new Arena(arenaName, arenaName, true);
                    break;
                case FFA:
                    a = new FFAArena(arenaName, arenaName, true);
                    break;
                case LTS:
                    a = new LTSArena(arenaName, arenaName, true);
                    break;
                case KC:
                    a = new KCArena(arenaName, arenaName, true);
                    break;
                default:
                    a = new Arena(arenaName, arenaName, true);
                    break;
            }

            Messenger.success(player, new MessageBuilder(Messages.ARENA_CREATE).replace(Tag.ARENA, a.toString(ChatColor.GREEN)).replace(Tag.STEPS, a.getSteps()).build());
        }
    }

    public String getName() {
        return "create";
    }

    public Messages getInfo() {
        return Messages.COMMAND_CREATE_INFO;
    }

    public String getArgs() {
        return "<arena> <" + ArenaType.getReadableList() + ">";
    }

    public String getPermission() {
        return "paintball.arena.create";
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

}