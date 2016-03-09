package me.synapz.paintball;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.synapz.paintball.commands.CommandManager;
import me.synapz.paintball.events.*;
import me.synapz.paintball.killcoin.KillCoinItem;
import me.synapz.paintball.killcoin.KillCoinListeners;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.metrics.Metrics;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.storage.Settings;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class Paintball extends JavaPlugin implements PluginMessageListener, Listener {

    @Override
    public void onEnable() {
        new Settings(this);
        this.setupEconomy();

        CommandManager commandManager = new CommandManager();
        commandManager.init();

        /*
        Important Things
        - Auto join teams don't work right sometimes
        - Listeners--
        - Permissions
        - More Coin
        - lives / hits under name --
        - Switch team wool numbers is wrong
        - Faster snowballs
        - Vault--

        Non-Important Things
        - TitleAPI Flicker--
        - Better ActionBar (It flickers and don't work sometimes)
        - Make it so if the config values are not found it sets it in. - (Still does not work for things not in default
        - Remove join signs on arena remove
        - Fix command order
        - Better stat sign layout
        - Fix leaderboard command
        - Global ranks in /pb stats also
        - Helmets don't show
        - Sometimes stats don't get set back on /reload
        - Team Pick bug where teams might not be an even number
        - Join Cooldown--
        - If player location is under 0 then kill them?--
        - Only update arena's values if it is not in progress (on /pb admin reload) bc dont want settings to change in game
        - Leave signs
        - Spectator join signs
        - they can buy items by just clicking on them... make sure the item isnt in their inventory...

        Future Features
        - Ranks
        - More KillCoins
        - Command hovers
            - Arena hovers, (who is in the arena, setup, etc)
        - Spectate Autojoin and signs
        - Holographic displays that pop out, like +1$ and +1 score etc.
        */

        // void die --
        // exp time countdown not working and count at 0
        // win message no prefix--
        // can move items in inventory in game

        Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinSigns(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatSystem(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LeaderboardSigns(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new KillCoinListeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "PaintBall", this);
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "PaintBall");

        getCommand("paintball").setExecutor(commandManager);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            ArenaManager.getArenaManager().updateAllSignsOnServer();
            Paintball.this.bungeeUpdateSigns();
        }, 0L, (long) Settings.SIGN_UPDATE_TIME);

        new KillCoinItem(Material.SNOW_BALL, ChatColor.RESET + Settings.THEME + "Paintball", 64, true, "Default Paintball Item", 0.0, 0, 0, "", Sound.CLICK);
        // 2
        new KillCoinItem(Material.SUGAR, "Sugar Overdose", 1, true, "Speeds up your movement by 2x", 100.50, 0, 0, "", Sound.BURP) {
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                ItemStack itemInHand = player.getItemInHand();
                Action action = event.getAction();

                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (60*20), 2));
                    player.getInventory().remove(itemInHand);
                }
            }
        };

        new KillCoinItem(Material.FIREBALL, "Flashbang", 1, true, "Throw the flashbang\nto stun your enemy.\n&lBe careful not to stun yourself!", 0.0, 0, 0, "", Sound.AMBIENCE_RAIN) {
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                player.launchProjectile(Fireball.class);
            }
            @EventHandler
            public void onFlashbangLand(ProjectileHitEvent e) {
                e.getEntity().sendMessage("hi");
                if (!(e.getEntity().getShooter() instanceof Player) && (ArenaManager.getArenaManager().getArena((Player) e.getEntity().getShooter()) == null))
                    return;
                Player player = (Player) e.getEntity().getShooter();
                Arena arena = ArenaManager.getArenaManager().getArena(player);
                PaintballPlayer pbPlayer = arena.getPaintballPlayer(player);

                player.sendMessage("Name: ");
                player.sendMessage("Custom Name: " + e.getEntity().getCustomName());
                if (e.getEntity().getName().equals(hasItemMeta() && getItemMeta().hasDisplayName() ? getItemMeta().getDisplayName() : "")) {
                    player.sendMessage("works");
                }
            }
        };

        // 5
        new KillCoinItem(Material.IRON_AXE, "Unlimited Paintballs", 1, true, "Receive an unlimited amount of Paintballs!", 0.0, 0, 120, "", Sound.CLICK) {
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                Action action = event.getAction();

                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    player.launchProjectile(Snowball.class);
                }
            }
        };

        // 7
        new KillCoinItem(Material.IRON_BARDING, "Machine Gun", 1, true, "Shoot many Paintballs at a time", 0.0, 0, 120, "", Sound.WOOD_CLICK) {
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                Action action = event.getAction();

                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    for (int i = 0; i < 10; i++) {
                        player.launchProjectile(Snowball.class);
                    }
                }
            }
        };

        // 10
        new KillCoinItem(Material.DIAMOND_BARDING, "Rocket Launcher", 1, true, "Shoot a giant wave of Paintballs", 0.0, 0, 0, "", Sound.ENDERDRAGON_DEATH) {
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                Action action = event.getAction();

                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    for (int i = 0; i < 50; i++) {
                        player.launchProjectile(Snowball.class);
                    }
                    player.getInventory().remove(player.getItemInHand());
                }
            }
        };

        new KillCoinItem(Material.ANVIL, "Tank", 1, true, "Become a Tank! You will have\nhave unlimited Paintballs, limited range, and move\nslow like a Tank.", 0.0, 0, 0, "", Sound.AMBIENCE_THUNDER) {
            @Override
            public void onClickItem(PlayerInteractEvent event){
                Player player = event.getPlayer();

                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60*20, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60*20, 1));
                player.setGameMode(GameMode.CREATIVE);
            }
        };

        // TODO: not working
        new KillCoinItem(Material.TNT, "Nuke", 1, true, "Click to kill everyone\n on the other team!", 0.0, 0, 0, "", Sound.ZOMBIE_REMEDY) {
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                Arena arena = ArenaManager.getArenaManager().getArena(player);
                ArenaPlayer arenaPlayer = (ArenaPlayer) arena.getPaintballPlayer(player);
                Team safeTeam = arenaPlayer.getTeam();

                for (ArenaPlayer gamePlayer : arena.getAllArenaPlayers()) {
                    Team team = gamePlayer.getTeam();
                    if (team != safeTeam) {
                        arenaPlayer.kill(gamePlayer);
                        player.teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.SPAWN))));
                    }
                }
                player.getInventory().remove(player.getItemInHand());
                event.setCancelled(true);
            }
        };

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();

            /*
            Metrics.Graph playersInArenaGraph = metrics.createGraph("Players In Arena");

            playersInArenaGraph.addPlotter(new Metrics.Plotter() {
                @Override
                public int getValue() {
                    return 22;
                }
            });
            */
        } catch (IOException exc) {

        }
    }

    @Override
    public void onDisable() {
        ArenaManager.getArenaManager().stopArenas();
    }

    //TODO: Find a good home for this
    public void bungeeUpdateSigns() {
        if (Settings.BUNGEE_CORD) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ArenaUpdate");
            out.writeUTF(Settings.SERVER_ID);
            //TODO: get arena states and names and make a string split with :'s
            // name:name:name
            // state:state:state
            // they must be in the correct order
            Bukkit.getServer().sendPluginMessage(this, "PaintBall", out.toByteArray());
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("PaintBall")) return;
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String command = in.readUTF();
            if (command.equals("IncomingPlayer")) {
                String uuid = in.readUTF();
                String aname = in.readUTF();
                String serverUUID = in.readUTF();
                UUID puuid = UUID.fromString(uuid);
                Player p = Bukkit.getPlayer(puuid);
                if (serverUUID.equalsIgnoreCase(getConfig().getString("ServerID"))) {
                    if (p != null) {
                        //TODO: add player to lobby of arena with name aname
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Settings.VAULT = false;
        } else {
            Settings.ECONOMY = rsp.getProvider();
        }
    }
}