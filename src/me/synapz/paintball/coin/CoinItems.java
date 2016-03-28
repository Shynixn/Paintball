package me.synapz.paintball.coin;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.Team;
import me.synapz.paintball.Utils;
import me.synapz.paintball.events.ArenaClickItemEvent;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoinItems implements Listener {

    private static CoinItems instance = null;

    private static Sound CLICK_SOUND;

    public enum Items {
        SUGAR_OVERDOSE,
        AK_47,
        MINI_GUN,
        SPRAY_N_PRAY,
        ROCKET_LAUNCHER,
        NUKE,
        TIME_WARP;

        @Override
        public String toString() {
            return super.toString().replace("_", "-").toLowerCase();
        }
    }

    static {
        try {
            CLICK_SOUND = Sound.BLOCK_LEVER_CLICK;
        } catch (NoSuchFieldError exc) {
            // Sound did not load... try to load the 1.8 and lower sound
            CLICK_SOUND = Sound.valueOf("CLICK");
        }
    }

    public static CoinItems getCoinItems() {
        if (instance == null) {
            new CoinItems().loadItems();
        }
        return instance;
    }

    public void loadItems() {
        instance = new CoinItems();

        new CoinItem(Items.AK_47) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();

                Projectile snowball = player.launchProjectile(Snowball.class);
                snowball.setVelocity(snowball.getVelocity().multiply(event.getArena().SPEED));
            }
        };

        new CoinItem(Items.SPRAY_N_PRAY) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();

                for (int i = 0; i < 20; i++)
                    Utils.shootSnowball(player, event.getArena(), 1);
            }
        };

        new CoinItem(Items.MINI_GUN) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();

                for (int i = 0; i < 20; i++) {
                    Projectile snowball = player.launchProjectile(Snowball.class);
                    snowball.setVelocity(snowball.getVelocity().multiply(event.getArena().SPEED));
                }
            }
        };

        new CoinItem(Items.SUGAR_OVERDOSE) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();
                ItemStack itemInHand = player.getItemInHand();

                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 2));
                player.getInventory().remove(itemInHand);
            }
        };

        new CoinItem(Items.ROCKET_LAUNCHER) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();
                for (int i = 0; i < 50; i++) {
                    Utils.shootSnowball(player, event.getArena(), 0);
                }
                player.getInventory().remove(player.getItemInHand());
            }
        };
        new CoinItem(Items.NUKE) {
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
        new CoinItem(Items.TIME_WARP) {
            public void onClickItem(ArenaClickItemEvent event) {
                ArenaPlayer player = event.getArenaPlayer();
                if (player.getLastLocation() == null) {
                    Messenger.error(player.getPlayer(), "You do not have a last location.");
                } else {
                    Messenger.success(player.getPlayer(), "Teleporting to last location...");
                    event.getCoinItem().remove(player);

                    player.getPlayer().teleport(player.getLastLocation());
                }
            }
        };
    }

    public CoinItem getMainItem() {
        CoinItem item = new CoinItem(Material.GOLD_BARDING, "Gun", 1, false, "Paintball Shooter", 0.0D, 0, 0, "", CLICK_SOUND) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();

                Utils.shootSnowball(player, event.getArena(), 0);
            }
        };
        return item;
    }
}