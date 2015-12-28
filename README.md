# Paintball
###Features
* Unlimited Arenas
* Up to 16 teams per arena
    * Each team with their own team color
    * Team colors shown in chat, HUD, name, and armor
    * Ability to change each team's name in config
* Join/Auto-join signs
    * Auto join signs will automatically join the arena with the most players
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
- [ ] add new, full for when
- [ ] click on leaderboard sign, show all their stats
- [ ] in game don’t let move things around
- [ ] add join sign permissions to default
- [x] add check on run to check if there are still enough players to start, if there isn’t cancel it
- [ ] /paintball admin item << gives item if player clicks
- [x] List<Arena> arenasPlayingTitle
- [ ] Add arena autojoin to join most ready arena
- [ ] Make arenas.yml file smaller (1 line locations, 1 line values, etc)-- Longer .yml files may lag so this will help to read files faster
- [ ] Add def to settings and send console error when it can't load
- [ ] Big bug where if the team and max aren't multiples of each other, the teams won't be evenly distrobuted. Ex: If max is 16 and there are 12 teams, 16/12 = 0.75 which rounds to 1 per team, so the plugin is currently making it 1 per team
