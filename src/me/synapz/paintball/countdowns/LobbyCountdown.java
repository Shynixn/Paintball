package me.synapz.paintball.countdowns;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.utils.MessageBuilder;

public class LobbyCountdown extends PaintballCountdown {

    public LobbyCountdown(int counter, Arena a) {
        super(a, counter); // adds 1 so to human eyes it goes from 5 to 1 instead of 4 to 0
    }

    public void onFinish() {
        arena.broadcastMessage(Messages.TELEPORTING);
        arena.startGame();
        this.cancel();
    }

    // Called every iteration of run()
    public void onIteration() {
        String prefix = Messages.ARENA_LOBBY_COUNTDOWN_HEADER.getString();
        String suffix = new MessageBuilder(Messages.ARENA_LOBBY_COUNTDOWN_FOOTER).replace(Tag.TIME, ((int) counter) + "").build();

        arena.broadcastMessage(prefix + " " + suffix);

        int fadeIn = 0;

        // If the counter is not in the no interval (...5.4..3...2..1) then we can fade in nicely because there will be no flicker
        if (!(counter <= arena.LOBBY_NO_INTERVAL)) {
            fadeIn = 20;
        }

        arena.broadcastTitle(prefix, suffix, fadeIn, 30, 20);
    }

    public boolean stop() {
        return arena.getState() != Arena.ArenaState.WAITING || (arena.getLobbyPlayers().size() < arena.getMin());
    }

    public boolean intervalCheck() {
        arena.updateAllScoreboardTimes();
        arena.updateSigns();

        return counter <= arena.LOBBY_NO_INTERVAL || counter % arena.LOBBY_INTERVAL == 0;
    }

    @Override
    public void cancel() {
        super.cancel();
        tasks.remove(arena, this);
        arena.updateSigns();
    }
}
