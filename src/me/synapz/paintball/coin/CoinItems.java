package me.synapz.paintball.coin;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.enums.Items;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.events.ArenaClickItemEvent;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CoinItems implements Listener {

    private static CoinItems instance = null;

    private CoinItem defaultItem;

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

                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Projectile snowball = player.launchProjectile(Snowball.class);
                    snowball.setVelocity(snowball.getVelocity().multiply(event.getArena().SPEED));
                }
            }
        };

        new CoinItem(Items.SPRAY_N_PRAY) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();

                for (int i = 0; i < 20; i++)
                    Utils.shootSnowball(player, event.getArena(), 0.2);
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

                // default duration
                int duration = 1200;

                for (PotionEffect effect : player.getActivePotionEffects()) {
                    if (effect.getType() == PotionEffectType.SPEED) {
                        duration += effect.getDuration();
                    }
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 2));
                player.getInventory().remove(itemInHand);
            }
        };

        new CoinItem(Items.COWBOY) {
            @Override
            public void onClickItem(ArenaClickItemEvent event) {
                Player rider = event.getArenaPlayer().getPlayer();
                Horse horse = (Horse) rider.getWorld().spawnEntity(rider.getLocation(), EntityType.HORSE);

                horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                horse.setTamed(true);
                horse.setAdult();
                horse.setOwner(rider);
                horse.setPassenger(rider);

                event.getArenaPlayer().setHorse(this, horse);

                rider.getInventory().remove(rider.getItemInHand());
            }
        };

        new CoinItem(Items.CYCLONE) {
            @Override
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();
                Location start = player.getLocation();

                for (int i = -180; i < 180; i++) {
                    player.teleport(new Location(start.getWorld(), start.getX(), start.getY(), start.getZ(), i, start.getPitch()));

                    Projectile snowball = player.launchProjectile(Snowball.class);
                    snowball.setVelocity(snowball.getVelocity().multiply(event.getArena().SPEED));
                }

                player.getInventory().remove(player.getItemInHand());
            }
        };

        new CoinItem(Items.DOUBLE) {
            @Override
            public void onClickItem(ArenaClickItemEvent event) {
                ArenaPlayer arenaPlayer = event.getArenaPlayer();

                Messenger.msg(arenaPlayer.getPlayer(), "Double coins activated for the rest of the game.");
                arenaPlayer.setMultiplier(2);

                arenaPlayer.getPlayer().getInventory().remove(arenaPlayer.getPlayer().getItemInHand());
            }
        };

        new CoinItem(Items.ROCKET_LAUNCHER) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();
                for (int i = 0; i < 50; i++) {
                    Utils.shootSnowball(player, event.getArena(), 0.3);
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
                        gamePlayer.setHealth(safeTeam, 0);
                        arenaPlayer.kill(gamePlayer, this.getAction());
                        gamePlayer.getPlayer().teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.SPAWN))));
                    }
                }
                arenaPlayer.getPlayer().getInventory().remove(arenaPlayer.getPlayer().getItemInHand());
            }
        };

        if (Paintball.IS_1_9) {
            new CoinItem(Items.DUEL_WIELD) {
                @Override
                public void onClickItem(ArenaClickItemEvent event) {
                    Player player = event.getArenaPlayer().getPlayer();
                    PlayerInventory inv = player.getInventory();

                    if (inv.getItemInOffHand() == null || inv.getItemInOffHand().getType() == Material.AIR) {
                        inv.setItemInOffHand(player.getItemInHand());
                        inv.setItemInMainHand(new ItemStack(Material.AIR));
                    } else {
                        Utils.shootSnowball(player, event.getArena(), 0.1);
                    }
                }
            };
        }

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

        new CoinItem(Items.PAINTBALL_SHOWER) {
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();

                for (int i = 0; i < 300; i++) {
                    Location loc = player.getLocation();
                    loc.setPitch(-90);
                    player.teleport(loc);
                    Utils.shootSnowball(player, event.getArena(), 0.2);
                }
                player.getInventory().remove(player.getItemInHand());
            }
        };

        new CoinItem(Items.FLY) {
            @Override
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();

                boolean toFly = !player.getAllowFlight();

                if (toFly)
                    Messenger.success(player, "Flying has been enabled");
                else
                    Messenger.success(player, "Flying has been disabled");

                player.setAllowFlight(toFly);
                player.setFlying(toFly);
            }
        };

        new CoinItem(Items.RAPID_FIRE) {
            @Override
            public void onClickItem(ArenaClickItemEvent event) {
                Player player = event.getArenaPlayer().getPlayer();

                Utils.shootSnowball(player, event.getArena(), 0.1);
            }
        };

        makeDefaultItem();
    }

    public CoinItem getDefaultItem() {
        makeDefaultItem();
        return defaultItem;
    }

    private void makeDefaultItem() {
        if (defaultItem == null) {
            defaultItem = new CoinItem(Items.DEFAULT) {
                @Override
                public void onClickItem(ArenaClickItemEvent event) {
                    Player player = event.getArenaPlayer().getPlayer();
                    Arena arena = event.getArena();

                    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                        Utils.shootSnowball(player, event.getArena(), arena.ACCURACY ? 0 : 0.1);
                }
            };
        }
    }
}