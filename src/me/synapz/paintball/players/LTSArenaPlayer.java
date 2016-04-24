package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.enums.ScoreboardLine;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.scoreboards.PaintballScoreboard;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Utils;
import org.bukkit.entity.Player;

public class LTSArenaPlayer extends ArenaPlayer {

    public LTSArenaPlayer(Arena a, Team t, Player p) {
        super(a, t, p);
    }

    @Override
    public PaintballScoreboard createScoreboard() {
        double bal = Settings.VAULT ? Settings.ECONOMY.getBalance(player) : 0;

        PaintballScoreboard sb = new PaintballScoreboard(this, arena.TIME, "Arena:")
                .addTeams(true)
                .addLine(ScoreboardLine.LINE)
                .addLine(ScoreboardLine.MONEY, arena.CURRENCY + bal, Settings.VAULT)
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



    @Override
    public void updateScoreboard() {
        if (pbSb == null)
            return;

        double bal = Settings.VAULT ? Settings.ECONOMY.getBalance(player) : 0;

        int size = arena.getArenaTeamList().size()-1;
        pbSb.reloadTeams(false)
                .reloadLine(ScoreboardLine.MONEY, arena.CURRENCY + bal, size+2)
                .reloadLine(ScoreboardLine.KD, getKd(), size+3)
                .reloadLine(ScoreboardLine.COIN, String.valueOf(getCoins()), size+4)
                .reloadLine(ScoreboardLine.KILL_STREAK, String.valueOf(getKillStreak()), size+5)
                .reloadLine(ScoreboardLine.KILLS, String.valueOf(getKills()), size+6)
                .reloadLine(ScoreboardLine.HEALTH, Utils.makeHealth(getHealth()), size+8)
                .reloadLine(ScoreboardLine.LIVES, Utils.makeHealth(getLives()), size+9, arena.LIVES > 0);
    }

}
