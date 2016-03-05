package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.Team;
import me.synapz.paintball.Utils;
import me.synapz.paintball.enums.ScoreboardLine;
import me.synapz.paintball.scoreboards.PaintballScoreboard;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.bukkit.ChatColor.*;
import static me.synapz.paintball.storage.Settings.*;

public final class SpectatorPlayer extends PaintballPlayer {

    private boolean asArenaPlayer;
    private PaintballScoreboard sb;

    /*
    This constructor will be used when a player types /pb spectate.
    Player -> SpectatorPlayer
     */
    public SpectatorPlayer(Arena a, Player p) {
        super(a, new Team(a), p);
        this.asArenaPlayer = false;
        player.teleport(arena.getSpectatorLocation());
    }

    /*
    When a player dies in the arena, they will be sent to spectate 3 blocks above their death spot.
    ArenaPlayer -> SpectatorPlayer
     */
    public SpectatorPlayer(ArenaPlayer arenaPlayer) {
        super(arenaPlayer.getArena(), new Team(arenaPlayer.getArena()), arenaPlayer.getPlayer());
        this.asArenaPlayer = true;
        player.teleport(player.getLocation().add(0, 3, 0));
    }

    protected void initPlayer() {
        PLAYERDATA.savePlayerInformation(player);

        Utils.stripValues(player);
        giveItems();
        displayMessages();
    }

    @Override
    protected void showMessages() {

    }

    protected String getChatLayout() {
        return arena.SPEC_CHAT;
    }

    public void chat(String message) {
        String chat = getChatLayout();

        chat = chat.replace("%MSG%", message);
        chat = chat.replace("%PREFIX%", PREFIX);
        chat = chat.replace("%PLAYER%", player.getName());

        for (Player player : arena.getAllPlayers().keySet()) {
            player.sendMessage(chat);
        }
    }

    public void forceLeaveArena() {
        if (team != null)
            team.playerLeaveTeam();
        // super.forceLeaveArena();
    }

    protected void giveItems() {
        // TODO: give item to leave and teleport
    }

    private void displayMessages() {
        Messenger.titleMsg(player, true, GREEN + "You are now spectating!");
    }

    @Override
    public PaintballScoreboard createScoreboard() {

        player.getScoreboard().getTeam(team.getTitleName()).setCanSeeFriendlyInvisibles(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
        arena.updateAllScoreboard();
        return new PaintballScoreboard(this, 1, "Spectator:")
                .addTeams(true)
                .build();
    }

    @Override
    public void updateScoreboard() {
        if (sb != null)
            sb.reloadTeams(true);
    }

    @Override
    public void updateDisplayName() {
        if (sb != null)
            sb.setDisplayNameCounter(Utils.getCurrentCounter(arena));
    }
}

