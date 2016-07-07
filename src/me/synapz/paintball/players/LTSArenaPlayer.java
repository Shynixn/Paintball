package me.synapz.paintball.players;

import me.synapz.paintball.enums.ScoreboardLine;
import me.synapz.paintball.scoreboards.PaintballScoreboard;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Utils;

public class LTSArenaPlayer extends ArenaPlayer {

    public LTSArenaPlayer(LobbyPlayer lobbyPlayer) {
        super(lobbyPlayer);
    }

    @Override
    public PaintballScoreboard createScoreboard() {
        double bal;

        if (Settings.USE_ECONOMY)
            bal = Settings.ECONOMY.getBalance(player);
        else
            bal = 0;

        PaintballScoreboard sb = new PaintballScoreboard(this, arena.TIME, "Arena:")
                .addTeams(false)
                .addLine(ScoreboardLine.LINE)
                .addLine(ScoreboardLine.MONEY, arena.CURRENCY + bal, Settings.USE_ECONOMY)
                .addLine(ScoreboardLine.KD, "0.00")
                .addLine(ScoreboardLine.COIN, 0, arena.COINS)
                .addLine(ScoreboardLine.KILL_STREAK, 0)
                .addLine(ScoreboardLine.KILLS, 0)
                .addLine(ScoreboardLine.LINE)
                .addLine(ScoreboardLine.HEALTH, Utils.makeHealth(arena.HITS_TO_KILL));
        if (arena.LIVES > 0)
            sb.addLine(ScoreboardLine.LIVES, Utils.makeHealth(arena.LIVES));
        return sb.build();
    }

}
