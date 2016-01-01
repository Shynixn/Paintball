# Paintball
###Features
* Unlimited Arenas
* Up to 16 teams per arena
    * Each team with their own team color
    * Team colors shown in chat, HUD, name, and armor
    * Ability to change each team's name in config
* Join/Auto-join signs
    * Auto join signs will automatically join the arena with the most players
* Auto updating signs
    * Join, autojoin, and leaderboard signs will be automatically updated with a configurable time
* Change Paintball colors to match your server
    * Many servers have chosen colors to match their server. You can make the plugin match your server or chat with two useful configs, theme-color, secondary-color, and prefix to make your plugin look custom to your server
* Safe reloading (/reload and /stop)
    * Players will be kicked, arenas will be reset, and items will be given back
* Ability to reload just the configuration, will not effect any arenas and gameplay
* Efficiently and safely works alongside all server types (I.e faction, mini games, etc)
* Simultaneous games
* Arena chat system
    * Set chat layout with tags (%TEAM%, %COLOR% etc.) in your config
    * Lobby chat, spectator chat, or if in game, arena chat
* Set team's specific lobby and game spawn points
* In depth step system to show you what you have to set before you can start the arena
* Per player statistics
    * Highest kill streak, K/D, kills, deaths, games player, wins, defeats, hits, shots, accuracy
    * Statistics are able to be synced accross servers with SQL
* BungeeCord support with the hub plugin(Comming Soon)
* Leaderboards
    * Leaderboard signs
    * Leaderboard command
    * Auto updates signs as leaderboards change
* Ability to turn off plugin msgs
* Designed using Object Oriented principals
    * Allows me to limit bugs, fix them faster, and add more features quicker
* TitleAPI (1.8+)

###Soon to come features
* Kill coins (earned per kill/win with a config number per kill/win)
* Kill coins shop, buy weapons (grenades, snipers, etc)
* Kits
* Scoreboard Support?
* SkyText Support (http://dev.bukkit.org/bukkit-plugins/skytext/)

###Bugs
Bugs
- [ ] EasySetupGUI?
- [ ] no commands in arena
- [ ] no item drop
- [ ] what if the spawn is in another world.... and the world isn't loaded
- [ ] pb stat page
- [ ] HUD color sometimes doesn’t go back to normal
- [ ] force leave doesn’t make the other person leave
- [ ] add config option to stop changing teams
- [ ] Set multiple spawn points?? /pb arena setspawn red <number>
- [ ] Leaderboard command /pb leaderboard <stat>
- [ ] Maybe place rank in my stat
- [ ] /stats values change on reloads
- [ ] stat signs fix layout
- [ ] commands out of order
- [ ] Get all config values loaded into plugin with checks
- [ ] Pb admin command <command>
- [ ] add start time to signs if arena is starting soon
- [ ] replace sign with Full if full
- [ ] should arena signs auto remove when arena is removed?
- [ ] click on leaderboard sign, show all their stats
- [ ] in game don’t let move things around
- [ ] add join sign permissions to default
- [ ] /paintball admin item << gives item if player clicks
- [ ] Add arena autojoin to join most ready arena
- [ ] Make arenas.yml file smaller (1 line locations, 1 line values, etc)-- Longer .yml files may lag so this will help to read files faster
- [ ] Add def to settings and send console error when it can't load
- [ ] Big bug where if the team and max aren't multiples of each other, the teams won't be evenly distrobuted. Ex: If max is 16 and there are 12 teams, 16/12 = 0.75 which rounds to 1 per team, so the plugin is currently making it 1 per team

####KillCoin API
Paintball has a powerful, built-in, object-oriented API for creating new KillCoins, giving you a higher ability to expand and customize your server.

There are two ways to create a new KillCoin item
1) Through Java - More functionality and customization
First, create a Bukkit plugin and setup the plugin.yml
Second, make a new KillCoinItem(material, name, description, money, killcoins, expirationTime, permission, amount, isShown)
An example is shown below
```java

    public class Plugin extends JavaPlugin {

        public void onEnable() {
        /*
        Adds a diamond axe, with the name Forward, a 2-lined description that describes what it does.
        A worth of $0, so it requires no money (This requires Vault if you want it to be worth something)
        A worth of 10 KillCoins, so requires and removes 10 KillCoins when bought
        An expiration time of 300 seconds (5 minutes)
        Requires permission "paintball.item.forward"
        Sets the amount to 1
        And is shown in the inventory, true

        You can set KillCoins, expiration time, or money to 0 to remove requiring it
        You can also set permission, or description safely to null in order to remove a description or required permission
         */
        new KillCoinItem(Material.DIAMOND_AXE, "Jumper", "Gives you the ability to jump\n5 blocks up!", 0, 10, 300, "paintball.item.sneaker", 1, true) {
            /*
            Whenever this item is right clicked, the player is teleported up 5 blocks by adding 5 to the Y
             */
            @Override
            public void onClickItem(PlayerInteractEvent event) {
                Player player = event.getPlayer();
                Action action = event.getAction();

                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    player.teleport(player.getLocation().add(0, 5, 0));
                }
            }
        };
      }
    }
```
2) Through config.yml - Extremely limited
In the config.yml, you will notice the Kill-Coin-Item section. Here is a snip of it:
```yaml
Kill-Coin-Shop:
  double-snow-ball-shooter:
    name: "&6Double Paintball Shooter"
    material: "SNOW_BALL"
    description: "&dGives you an extra speed Paintball launcher\n&dthat can shoot two paintballs at a time!"
    permission-required: "paintball.shop.doubleshooter"
    amount: 1
    expiration-time: -1
    money: 120
    killcoins: 2
    shown: true
```
You can add new items to the shop by making a new section. For example, if you wanted to add a new KillCoin item with the same values of the double-snow-ball-shooter, you can copy and paste it and change the values. For exmaple:
```yaml
Kill-Coin-Shop:
  double-snow-ball-shooter:
    name: "&6Double Paintball Shooter"
    material: "SNOW_BALL"
    description: "&dGives you an extra speed Paintball launcher\n&dthat can shoot two paintballs at a time!"
    permission-required: "paintball.shop.doubleshooter"
    amount: 1
    expiration-time: -1
    money: 120
    killcoins: 2
    shown: true
  double-shooter:
    name: "&6Double Paintball Shooter"
    material: "SNOW_BALL"
    description: "&dGives you an extra speed Paintball launcher\n&dthat can shoot two paintballs at a time!"
    permission-required: "paintball.shop.doubleshooter.more"
    amount: 64
    expiration-time: -1
    money: 5000
    killcoins: 0
    shown: true
```
There are a few downsides to this way:
* You can only edit an existing item that is stored in config.yml, this means name: must be the same as another item in config
* No special effects can be added to the new items, this can only be done through way #1 or just creating a new listener for this item without using the API
