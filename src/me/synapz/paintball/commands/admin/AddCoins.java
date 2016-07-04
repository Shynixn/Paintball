package me.synapz.paintball.commands.admin;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AddCoins extends PaintballCommand {

    // /pb admin addcoins <player> <amount>

    @Override
    public void onCommand(Player player, String[] args) {
        String targetStr = args[2];
        String coinStr = args[3];
        int coins;

        if (Bukkit.getPlayer(targetStr) == null) {
            Messenger.error(player, "Target '" + targetStr + "' was not found.");
            return;
        }

        Player target = Bukkit.getPlayer(targetStr);
        Arena targetArena = getArena(Bukkit.getPlayer(targetStr));

        if (targetArena == null) {
            Messenger.error(player, "Target player is not an arena");
            return;
        }

        try {
            coins = Integer.parseInt(coinStr);
        } catch (NumberFormatException e) {
            Messenger.error(player, coinStr + " is not a valid number!");
            return;
        }

        if (coins <= 0) {
            Messenger.error(player, "Amount must be greater then 0.");
            return;
        }

        if (targetArena.getState() != Arena.ArenaState.IN_PROGRESS) {
            Messenger.error(player, "Arena is not in progress.");
            return;
        }

        PaintballPlayer pbPlayer = targetArena.getPaintballPlayer(target);

        if (!(pbPlayer instanceof ArenaPlayer)) {
            Messenger.error(player, "Target must be an arena player!");
            return;
        }

        ArenaPlayer targetArenaPlayer = (ArenaPlayer) pbPlayer;

        targetArenaPlayer.depositCoin(coins);
        targetArena.updateAllScoreboard();
    }

    @Override
    public String getName() {
        return "addcoin";
    }

    @Override
    public Messages getInfo() {
        return Messages.COMMAND_ADDCOIN_INFO;
    }

    @Override
    public String getArgs() {
        return "<player> <amount>";
    }

    @Override
    public String getPermission() {
        return "paintball.admin.addcoin";
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    @Override
    public int getMaxArgs() {
        return 4;
    }

    @Override
    public int getMinArgs() {
        return 4;
    }

    private Arena getArena(Player player) {
        return ArenaManager.getArenaManager().getArena(player);
    }
}

