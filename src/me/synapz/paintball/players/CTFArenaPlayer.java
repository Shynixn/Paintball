package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.CTFArena;
import me.synapz.paintball.Team;
import me.synapz.paintball.Utils;
import me.synapz.paintball.locations.FlagLocation;
import me.synapz.paintball.storage.Settings;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CTFArenaPlayer extends ArenaPlayer {

    private CTFArena ctfArena = (CTFArena) arena;
    private boolean isFlagHolder = false;
    private Team heldFlag;

    public CTFArenaPlayer(Arena a, Team t, Player p) {
        super(a, t, p);
    }

    @Override
    public void kill(ArenaPlayer arenaPlayer, String action) {
        CTFArenaPlayer ctfArenaPlayer = (CTFArenaPlayer) arenaPlayer;

        super.kill(ctfArenaPlayer, action);

        if (ctfArenaPlayer.isFlagHolder())
            ctfArenaPlayer.dropFlag();
    }

    public void pickupFlag(Team pickedUp) {

        if (pickedUp != null && pickedUp == team)
            return;

        Location flagLoc = new FlagLocation((CTFArena) arena, pickedUp).getLocation();

        isFlagHolder = true;
        heldFlag = pickedUp;

        flagLoc.getBlock().setType(Material.AIR);

        arena.broadcastMessage(Settings.THEME + ChatColor.BOLD + player.getName() + " has stolen " + pickedUp.getTitleName() + "'s flag!");

        player.getInventory().setHelmet(Utils.makeBanner(team.getChatColor() + team.getTitleName() + " Flag", pickedUp.getDyeColor()));
        player.updateInventory();

        if (ctfArena.getDropedFlagLocations().containsKey(flagLoc))
            ctfArena.remFlagLocation(flagLoc);
    }

    public void dropFlag() {

        ctfArena.addFlagLocation(getLastLocation(), heldFlag);
        makeBanner(heldFlag, getLastLocation());

        // Sends messages & removes the banner helmet
        arena.broadcastMessage(Settings.THEME + ChatColor.BOLD + player.getName() + " has dropped the flag!");

        player.getInventory().setHelmet(Utils.makeWool(team.getChatColor() + team.getTitleName() + " Team", team.getDyeColor()));
        player.updateInventory();

        isFlagHolder = false;
        heldFlag = null;
    }

    public boolean isFlagHolder() {
        return isFlagHolder;
    }

    private void makeBanner(Team dropedTeam, Location loc) {
        Block block = loc.getBlock();

        block.setType(Material.STANDING_BANNER);

        Banner banner = (Banner) block.getState();

        banner.setBaseColor(dropedTeam.getDyeColor());
        banner.update();
    }
}
