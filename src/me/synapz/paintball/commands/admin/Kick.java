package me.synapz.paintball.commands.admin;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Jeremy Lugo on 6/26/2016.
 */
public class Kick extends PaintballCommand{
    @Override
    public void onCommand(Player player, String[] args) {
        String playerName = args[2];
        Player toKick = Bukkit.getPlayer(playerName);
        if (toKick == null || ArenaManager.getArenaManager().getArena(toKick) == null) {
            Messenger.error(player, new MessageBuilder(Messages.PLAYER_NOT_IN_ARENA).replace(Tag.PLAYER, playerName).build());
            return;
        }

        Arena arena = ArenaManager.getArenaManager().getArena(toKick);
        PaintballPlayer paintballPlayer = arena.getPaintballPlayer(toKick);
        paintballPlayer.leave();

        Messenger.success(player, new MessageBuilder(Messages.KICK_PLAYER).replace(Tag.PLAYER, playerName).replace(Tag.ARENA, arena.getName()).build());
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public Messages getInfo() {
        return Messages.COMMAND_KICK_INFO;
    }

    @Override
    public String getArgs() {
        return "<player>";
    }

    @Override
    public String getPermission() {
        return "paintball.admin.kick";
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public int getMinArgs() {
        return 3;
    }
}
