package me.synapz.paintball.commands.admin;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.utils.MessageBuilder;
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
            Messenger.error(player, new MessageBuilder(Messages.NOT_FOUND).replace(Tag.PLAYER, targetStr).build());
            return;
        }

        Player target = Bukkit.getPlayer(targetStr);
        Arena targetArena = getArena(Bukkit.getPlayer(targetStr));

        if (targetArena == null) {
            Messenger.error(player, Messages.TARGET_NOT_IN_ARENA.getString());
            return;
        }

        try {
            coins = Integer.parseInt(coinStr);
        } catch (NumberFormatException e) {
            Messenger.error(player, Messages.VALID_NUMBER.getString());
            return;
        }

        if (coins <= 0) {
            Messenger.error(player, Messages.AMOUNT_GREATER_THAN_0.getString());
            return;
        }

        if (targetArena.getState() != Arena.ArenaState.IN_PROGRESS) {
            Messenger.error(player, Messages.ARENA_NOT_IN_PROGRESS.getString().replace(Tag.ARENA + "", targetArena.getName()));
            return;
        }

        PaintballPlayer pbPlayer = targetArena.getPaintballPlayer(target);

        if (!(pbPlayer instanceof ArenaPlayer)) {
            Messenger.error(player, Messages.MUST_BE_ARENA_PLAYER.getString());
            return;
        }

        ArenaPlayer targetArenaPlayer = (ArenaPlayer) pbPlayer;

        Messenger.success(player, new MessageBuilder(Messages.GAVE_COINS).replace(Tag.AMOUNT, coins + "").replace(Tag.PLAYER, targetArenaPlayer.getPlayer().getName()).build());
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

