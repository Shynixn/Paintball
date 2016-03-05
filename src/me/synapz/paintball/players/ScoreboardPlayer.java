package me.synapz.paintball.players;

import me.synapz.paintball.scoreboards.PaintballScoreboard;

public interface ScoreboardPlayer {

    PaintballScoreboard createScoreboard();

    void updateScoreboard();

    void updateDisplayName();
}
