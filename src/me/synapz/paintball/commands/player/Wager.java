package me.synapz.paintball.commands.player;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.events.WagerEvent;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Messenger;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


/**
 * Created by Jeremy Lugo on 6/21/2016.
 */
public class Wager extends PaintballCommand {

    @Override
    public void onCommand(Player player, String[] args) {
        Arena arena = getArena(player);
        String wagerString = args[1];
        int wager;

        if (arena == null) {
            Messenger.error(player, "You must be in an arena to wager!");
            return;
        }

        try {
            wager = Integer.parseInt(wagerString);
        } catch (NumberFormatException e) {
            Messenger.error(player, wagerString + " is not a valid number!");
            return;
        }

        if (wager <= 0) {
            Messenger.error(player, "Wager must be greater than zero!");
            return;
        }

        PaintballPlayer paintballPlayer = arena.getPaintballPlayer(player);

        if (paintballPlayer == null) {
            Messenger.error(player, "You must be in an arena to wager!");
            return;
        }

        EconomyResponse response = Settings.ECONOMY.withdrawPlayer(player.getName(), wager);
        WagerEvent event = new WagerEvent(paintballPlayer, arena, wager, response.transactionSuccess()
                ? WagerEvent.WagerResult.SUCCESS : WagerEvent.WagerResult.FAILURE);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public String getName() {
        return "wager";
    }

    @Override
    public Messages getInfo() {
        return Messages.COMMAND_WAGER_INFO;
    }

    @Override
    public String getArgs() {
        return "<amount>";
    }

    @Override
    public String getPermission() {
        return "paintball.wager";
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.PLAYER;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    private Arena getArena(Player player) {
        return ArenaManager.getArenaManager().getArena(player);
    }
}
