# lifed
Replicates life series functionality.<br>
**Please download at [Modrinth](https://modrinth.com/mod/lifed/versions)**


# For Players
If you cannot move, that is not a problem, it is because the session has not started, tell the server owner if you think it is a problem.
## Commands
- `/lives` : tells you how many lives you have
- `/givelife <PLAYER>` : will give one of your lives ( as long as you have more than 1 ) to `<PLAYER>`
- `/remaining` : will tell you how much time is left in the session, or how much is left in the break
- `/boogeys` : Will tell the you if there are any boogeymen at all remaining, not the count.
- `/amiboogey` : Tells a player if they are currently the boogey or not 

# For Server Admins
This mod replicates Last Life with customisability. It has session timing, item banning and more.<br>
Messages sent to players by the mod (like boogey titles) can be customised with a language file (see [en_us.json](https://github.com/z3roco01/lifed/tree/master/src/main/resources/assets/lifed/lang/en_us.json) for ids).
## Config file
If your config file is missing any of these properties, then stop your server, delete/rename it, then restart it. You should have the file full of defaults with every property.
The config file is in `config/lifed.conf`
### Properties
you can use [Minecraft formatting codes](https://minecraft.wiki/w/Formatting_codes) in any text 
- `maxBoogeymen` (default: 10) : The maximum number of boogeymen that will be rolled by default ( can still be overriden when running the command )
- `lightningOnRedDeath` (default: 5) : How many lightning bolts will be spawned on a red death
- `bannedItems` (default: ["minecraft:bookshelf"]) : A list containing item ids that will be banned, meaning they cannot be crafted or picked up, will be destroyed on pickup
- `uncraftableItems` (default: ["minecraft:enchanting_table"]) : A list of item ids which are just uncraftable, can still be picked up
- `bannedEffects` (default: ["minecraft:strength"]) : A list of effect ids that cannot be applied to players, they can still make potions, but if the effect is applied itll disappear immediately
- `highLevelPvpEnchAllowed` (default: false) : If false, all PVP enchantments cannot be applied at levels higher than 1
- `highLevelOtherEnchAllowed` (default: false) : If false, all non-PVP enchantments cannot be applied at levels higher than 1
- `expandedWolfSpawning` (default: true) : If true, lets wolves spawn in more biomes ( for now just flower and birch forests )
- `sessionLength` (default: 180) : How many minutes a session lasts
- `breakLength` (default: 10) : How many minutes one break lasts
- `startSessionTimer` (default: false) : Should the session timer start when the server does, will freeze all players at the start and require an op to start the session
- `lockoutPlayers` (default: true) : Should players be locked out of joining after boogeys have been rolled
- `totemsConvertable` (default: true) : When true, players can right click with a totem in their hand to turn it into another life
- `wolfLimit` (default: 5) : The maximum amount of wolves one player can have, set to -1 to disable
- `sequentialBoogeyChance` (default: 0.5) : The chance for another boogey to be chosen after the first. The first boogey is always guaranteed, and each boogey has this chance of happening. Ex: 1st 100%, 2nd 50%, 3rd 25%, 4th 12.5%
- `watcherDebug` (default: false) : Enables watcher debug commands when true, they may be undocumented or cheaty, so not recommended
- `logEvents` (default: false) : When true, events like boogey kills, cures, life changes, and life gifts are logged to the **server console**
- `mendingBanned` (default: true) : When true, mending cannot be rolled or applied to any item
- `soulmateLives` (default: 4) : How many lives everyone will start with after soulmate rolls
- `revealSoulmates` (default: false) : Should the pairs have their soulmates reveal to each other after rolling
## Commands
for any command that accepts `<PLAYERS>`, typing `@a` instead of a username will pick every current player
### `/watcher boogeyman`
- `roll` : rolls up to the maximum amount of boogeys, also waits 5 minutes ( each boogey is half as likely as the last, first is 100%)
- `roll <MAX>` : rolls boogeys up to the new supplied `<MAX>`
- `cure <PLAYER>` : cures one player, used if they got something like a trap kill
- `fail <PLAYER>` : fails a player, setting them to red life right away only if they are a boogey
- `failall` : fails all remaining boogeys
- `reset` : removes all boogeys without curing or failing
### `/watcher lives`
- `roll <PLAYERS>` : rolls `<PLAYERS>` lives between 2 and 6
- `set <PLAYERS> <LIVES>` : sets `<PLAYERS>` lives to `<LIVES>`
- `inc <PLAYER>` : gives `<PLAYER>` one more life
- `dec <PLAYER>` : removes one life from `<PLAYER>`
### `/watcher session`
controls things about the current session
- `start` : Starts the session with the time set in the config
- `start <LENGTH>` : Starts a session lasting `<LENGTH>` minutes
- `unpause` : will unpause the current session
- `pause` : will pause the current session ( not a break )
- `break` : starts a break for the set amount of time
- `cancelbreak` : stop a break that is currently happening
### `/watcher debug`
debugging and maybe cheaty commands, some may be undocumented
- `boogeychance <CHANCE>` : sets `sequentialBoogeyChance` to `<CHANCE>` ( does not save it )
- `instantboogey` : rolls immediately, without waiting 5 minutes
### `/watcher lock`
Handles the session lock, which is also enabled by the boogey roll ( if enabled )
- `status` : will tell you if the lock is currently on or off
- `toggle` : toggles the lock on and off, when toggled on, sets the list to the currently online players
- `add <PLAYERS>` : adds the passed players to the allowed players list
- `adduuid <UUID>` : adds a player from the passed uuid
### `/watcher soulmates`
Handles soulmate pairs
- `roll`: Rerolls all soulmates
- `clear`: Cleasr all soulmate pairs