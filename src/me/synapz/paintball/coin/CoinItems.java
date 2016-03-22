package me.synapz.paintball.coin;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.Team;
import me.synapz.paintball.Utils;
import me.synapz.paintball.events.ArenaClickItemEvent;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CoinItems implements Listener {

    private static CoinItems instance = null;

    public static CoinItems getCoinItems() {
        if (instance == null) {
            new CoinItems().loadItems();
        }
        return instance;
    }

    // TODO: Re set the coins.
    public void loadItems() {
        instance = new CoinItems();

        new CoinItem(Material.GOLD_BARDING, "AK-47", 1, true, "Shoot from an AK-47", 0.0D, 0, 0, "", Sound.CLICK) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();

                Projectile snowball = player.launchProjectile(Snowball.class);
                snowball.setVelocity(snowball.getVelocity().multiply(event.getArena().SPEED));
            }
        };

        new CoinItem(Material.IRON_BARDING, "Spray n' Pray", 1, true, "Spray tons of Paintballs\nand hit your enemy!", 0.0D, 0, 10, "", Sound.CLICK) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();

                for (int i = 0; i < 20; i++)
                    Utils.shootSnowball(player, event.getArena(), 1);
            }
        };

        new CoinItem(Material.GOLD_BARDING, "Mini-Gun", 1, true, "High Precision fast shooting gun", 0.0D, 0, 10, "", Sound.CLICK) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();

                for (int i = 0; i < 20; i++) {
                    Projectile snowball = player.launchProjectile(Snowball.class);
                    snowball.setVelocity(snowball.getVelocity().multiply(event.getArena().SPEED));
                }
            }
        };

        new CoinItem(Material.SUGAR, "Sugar Overdose", 1, true, "Speeds up your movement by 2x", 0.0D, 0, 0, "", Sound.CLICK) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();
                ItemStack itemInHand = player.getItemInHand();

                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 2));
                player.getInventory().remove(itemInHand);
            }
        };
        new CoinItem(Material.DIAMOND_BARDING, "Rocket Launcher", 1, true, "Shoot a giant wave of Paintballs", 0.0D, 0, 0, "", Sound.ENDERDRAGON_DEATH) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();
                for (int i = 0; i < 50; i++) {
                    Utils.shootSnowball(player, event.getArena(), 0);
                }
                player.getInventory().remove(player.getItemInHand());
            }
        };
        new CoinItem(Material.TNT, "Nuke", 1, true, "Click to kill everyone\n on the other team!", 0.0D, 0, 0, "", Sound.AMBIENCE_RAIN) {
            public void onClickItem(ArenaClickItemEvent event) {
                Team safeTeam = event.getArenaPlayer().getTeam();
                Arena arena = event.getArena();
                ArenaPlayer arenaPlayer = event.getArenaPlayer();
                for (ArenaPlayer gamePlayer : arena.getAllArenaPlayers()) {
                    Team team = gamePlayer.getTeam();
                    if (team != safeTeam) {
                        gamePlayer.setHealth(1);
                        arenaPlayer.kill(gamePlayer);
                        gamePlayer.getPlayer().teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.SPAWN))));
                    }
                }
                arenaPlayer.getPlayer().getInventory().remove(arenaPlayer.getPlayer().getItemInHand());
            }
        };
        new CoinItem(Material.GHAST_TEAR, "Time Warp", 1, true, "Teleports you to your\nlast death location", 0.0D, 0, 0, "", Sound.CLICK) {
            public void onClickItem(ArenaClickItemEvent event) {
                ArenaPlayer player = event.getArenaPlayer();
                if (player.getLastLocation() == null) {
                    Messenger.error(player.getPlayer(), "You do not have a last location.");
                } else {
                    Messenger.success(player.getPlayer(), "Teleporting to last location...");
                    // TODO: DOES THIS WORK!?
                    event.getCoinItem().remove(player);

                    player.getPlayer().teleport(player.getLastLocation());
                }
            }
        };
    }

    public CoinItem getMainItem() {
        CoinItem item = new CoinItem(Material.GOLD_BARDING, "Gun", 1, false, "Paintball Shooter", 0.0D, 0, 0, "", Sound.CLICK) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();

                Utils.shootSnowball(player, event.getArena(), 0);
            }
        };
        return item;
    }
}