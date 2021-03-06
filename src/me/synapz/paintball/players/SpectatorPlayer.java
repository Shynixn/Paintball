package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.enums.*;
import me.synapz.paintball.scoreboards.PaintballScoreboard;
import me.synapz.paintball.storage.PlayerData;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.storage.files.UUIDPlayerDataFile;
import me.synapz.paintball.storage.files.UUIDStatsFile;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.bukkit.ChatColor.*;

public class SpectatorPlayer extends PaintballPlayer {

    private UUIDPlayerDataFile uuidPlayerDataFile;
    private ArenaPlayer oldArenaPlayer;

    public SpectatorPlayer(Arena a, Player p) {
        super(a, new Team(a), p, true);
        player.teleport(arena.getSpectatorLocation());
    }

    public SpectatorPlayer(ArenaPlayer arenaPlayer) {
        super(arenaPlayer.getArena(), new Team(arenaPlayer.getArena()), arenaPlayer.getPlayer(), false);
        this.oldArenaPlayer = arenaPlayer;
        player.teleport(player.getLocation().add(0, 0.5, 0));
    }

    @Override
    protected void initPlayer(boolean storeData) {
        if (storeData) {
            playerData = new PlayerData(this);
            uuidPlayerDataFile = new UUIDPlayerDataFile(player.getUniqueId());
            uuidPlayerDataFile.savePlayerInformation();
        }

        player.setAllowFlight(true);
        player.setFlying(true);

        // Hides all spectator players from game players
        for (ArenaPlayer arenaPlayer : arena.getAllArenaPlayers()) {
            arenaPlayer.getPlayer().hidePlayer(player);
        }
    }

    @Override
    protected void giveItems() {
        player.getInventory().setItem(0, Utils.getSkull(this.getPlayer(), RED + "" + Settings.THEME + String.valueOf(BOLD) + Messages.CLICK.getString() + Messenger.SUFFIX + RESET + Settings.SECONDARY + Messages.Teleporter.getString()));
        player.getInventory().setItem(8, Utils.makeItem(Material.BED, Messages.ITEM_LEAVE_ARENA.getString(), 1));
        player.updateInventory();
    }

    @Override
    protected void showMessages() {
        Messenger.titleMsg(player, true, Messages.YOU_ARE_NOW_SPECTATING.getString());
    }

    @Override
    public PaintballScoreboard createScoreboard() {
        PaintballScoreboard scoreboard = new PaintballScoreboard(this, 1, "Spectator:")
                .addTeams(false)
                .addLine(ScoreboardLine.LINE, Settings.USE_ECONOMY)
                .addLine(ScoreboardLine.WAGER, arena.CURRENCY + arena.getWagerManager().getWager(), Settings.USE_ECONOMY)
                .build();
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
        arena.updateAllScoreboard();
        return scoreboard;
    }

    @Override
    public void updateScoreboard() {
        int size = arena.getActiveArenaTeamList().size()-1;

        if (pbSb != null)
            pbSb.reloadTeams(false);

        if (Settings.USE_ECONOMY && pbSb != null)
            pbSb.reloadLine(ScoreboardLine.WAGER, arena.CURRENCY + arena.getWagerManager().getWager(), size+2);
    }

    @Override
    public void leave() {
        super.leave();
        player.removePotionEffect(PotionEffectType.INVISIBILITY);

        if (oldArenaPlayer != null) {
            UUIDStatsFile uuidStatsFile = oldArenaPlayer.uuidStatsFile;
            uuidStatsFile.incrementStat(StatType.GAMES_PLAYED, oldArenaPlayer);
            uuidStatsFile.addToStat(StatType.HITS, oldArenaPlayer.hits);
            uuidStatsFile.addToStat(StatType.SHOTS, oldArenaPlayer.shots);
            uuidStatsFile.addToStat(StatType.KILLS, oldArenaPlayer.kills);
            uuidStatsFile.addToStat(StatType.DEATHS, oldArenaPlayer.deaths);

            // killstreak is less than past killstreak, return
            if (uuidStatsFile.getFileConfig().getInt(StatType.HIGEST_KILL_STREAK.getPath()) < oldArenaPlayer.heightKillStreak)
                uuidStatsFile.setStat(StatType.HIGEST_KILL_STREAK, oldArenaPlayer.heightKillStreak);

            uuidStatsFile.saveFile();
        }
    }

    public void openMenu() {
        player.openInventory(arena.getSpectatorInventory());
    }

    public void spectate(ArenaPlayer arenaPlayer) {
        Player target = arenaPlayer.getPlayer();
        player.teleport(target);
        Messenger.success(player, new MessageBuilder(Messages.NOW_SPECTATING).replace(Tag.PLAYER, target.getName()).build());
    }

    @Override
    public void updateDisplayName() {
        if (pbSb != null) {
            pbSb.setDisplayNameCounter("", Utils.getCurrentCounter(arena));
        }
    }
}
