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
* Kill coins
    * Earn coins per kill to buy items inside the Coin Shop
    * Comes with Coin API so you can add your own items to the Coin Shop
* Scoreboard
    * Show many values in game
    * Does not break plugins like FeatherBoard which takes advantage of Scoreboards
###Soon to come features
* Kill coins shop, buy weapons (grenades, snipers, etc)
* SkyText Support (http://dev.bukkit.org/bukkit-plugins/skytext/)
* Minema Recording of Paintball to feature on plugin page
* McStats
* Add hover information when someone hovers over command to show detailed description

####Coin API
Paintball has a powerful, built-in, object-oriented API for creating new Coins, giving you a higher ability to expand and customize your server.

There are two ways to create a new Coin item
1) Through Java - More functionality and customization
First, create a Bukkit plugin and setup the plugin.yml
Second, make a new CoinItem(material, name, description, money, coins, expirationTime, permission, amount, isShown)
An example is shown below
```java

    public class Plugin extends JavaPlugin {

        public void onEnable() {
        /*
        Adds a diamond axe, with the name Forward, a 2-lined description that describes what it does.
        A worth of $0, so it requires no money (This requires Vault if you want it to be worth something)
        A worth of 10 Coins, so requires and removes 10 Coins when bought
        An expiration time of 300 seconds (5 minutes)
        Requires permission "paintball.item.forward"
        Sets the amount to 1
        And is shown in the inventory, true

        You can set Coins, expiration time, or money to 0 to remove requiring it
        You can also set permission, or description safely to null in order to remove a description or required permission
         */
        new CoinItem(Material.DIAMOND_AXE, "Jumper", "Gives you the ability to jump\n5 blocks up!", 0, 10, 300, "paintball.item.sneaker", 1, true) {
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
    coins: 2
    shown: true
```
You can add new items to the shop by making a new section. For example, if you wanted to add a new Coin item with the same values of the double-snow-ball-shooter, you can copy and paste it and change the values. For exmaple:
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
    coins: 2
    shown: true
  double-shooter:
    name: "&6Double Paintball Shooter"
    material: "SNOW_BALL"
    description: "&dGives you an extra speed Paintball launcher\n&dthat can shoot two paintballs at a time!"
    permission-required: "paintball.shop.doubleshooter.more"
    amount: 64
    expiration-time: -1
    money: 5000
    coins: 0
    shown: true
```
There are a few downsides to this way:
* You can only edit an existing item that is stored in config.yml, this means name: must be the same as another item in config
* No special effects can be added to the new items, this can only be done through way #1 or just creating a new listener for this item without using the API
