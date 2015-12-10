package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Team;
import org.bukkit.entity.Player;

import static me.synapz.paintball.storage.Settings.*;


public class ArenaPlayer extends PaintballPlayer {

    public ArenaPlayer(Arena a, Team t, Player p) {
        super(a, t, p);

        a.addArenaPlayer(this);
        initPlayer();
    }

    public void chat(String message) {
        String chat = ARENA_CHAT;

        chat = chat.replace("%TEAMNAME%", team.getTitleName());
        chat = chat.replace("%TEAMCOLOR%", team.getChatColor() + "");
        chat = chat.replace("%MSG%", message);
        chat = chat.replace("%PREFIX%", PREFIX);
        chat = chat.replace("%PLAYER%", player.getName());
        for (PaintballPlayer pbPlayer : arena.getAllPlayers().values()) {
            pbPlayer.getPlayer().sendMessage(chat);
        }
    }

    protected void initPlayer() {
        player.teleport(arena.getSpawn(team));
        // TODO: openKit menu, stop from being able to move
    }

    public void leaveArena() {
        // TODO: remove player from arena
    }
}