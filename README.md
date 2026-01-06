# lifed
Replicates life series functionality

# For Players
If you cannot move, that is not a problem, it is because the session has not started, tell the server owner if you think it is a problem.
## Commands
- `/lives` : tells you how many lives you have
- `/givelife <PLAYER>` : will give one of your lives ( as long as you have more than 1 ) to `<PLAYER>`
- `/time` : will tell you how much time is left in the session, or how much is left in the break

# For Server Admins
This mod replicates Last Life with customisability. It has session timing, item banning and more.
## Config file
If your config file is missing any of these properties, then stop your server, delete/rename it, then restart it. You should have the file full of defaults with every property.
The config file is in `config/lifed.conf`
### Properties
you can use [minecraft formatting codes](https://minecraft.wiki/w/Formatting_codes) in any text 
- `maxBoogeymen` (default: 10) : The maximum number of boogeymen that will be rolled by default ( can still be overriden when running the command )
- `youAre` (default: "You are...") : The text shown in the title shown just before your boogey status is shown
- `notABoogey` (default: "...NOT a boogeyman !!!") : Title shown after you are revealed to not be a boogey
- `aBoogeyman` (default: "...A BOOGEYMAN !!!") : Title shown when you are a boogeyman
- `boogeyChatMsg` : Chat message sent to all boogeys explaining the rules
- `lightningOnRedDeath` (default: 5) : How many lightning bolts will be spawned on a red death
- `bannedItems` (default: ["minecraft:bookshelf"]) : A list containing item ids that will be banned, meaning they cannot be crafted or picked up, will be destroyed on pickup
- `uncraftableItems` (default: ["minecraft:enchanting_table"]) : A list of item ids which are just uncraftable, can still be picked up
- `bannedEffects` (default: ["minecraft:strenght"]) : A list of effect ids that cannot be applied to players, they can still make potions, but if the effect is applied itll disappear immediately
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
## Commands
for any command that accepts `<PLAYERS>`, typing `@a` instead of a username will pick every current player
### `/watcher boogeyman`
- `roll` : rolls up to the maximum amount of boogeys, also waits 5 minutes ( each boogey is half as likely as the last, first is 100%)
- `roll <MAX>` : rolls boogeys up to the new supplied `<MAX>`
- `instantroll` : rolls immediately, without waiting 5 minutes
- `cure <PLAYER>` : cures one player, used if they got something like a trap kill
- `fail <PLAYERS>` : fails players, setting them to red life right away
- `reset` : removes all boogeys without curing or failing
- `remaining` : informs an admin if there are any boogeys remaining ( not how many )
### `/watcher lives`
- `roll <PLAYERS>` : rolls `<PLAYERS>` lives between 2 and 6
- `set <PLAYERS> <LIVES>` : sets `<PLAYERS>` lives to `<LIVES>`
- `inc <PLAYER>` : gives `<PLAYER>` one more life
- `dec <PLAYER>` : removes one life from `<PLAYER>`
### `/watcher session`
controls things about the current session
- `start` : will unpause the current session
- `stop` : will pause the current session ( not a break )
- `break` : starts a break for the set amount of time
- `cancelbreak` : stop a break that is currently happening
### `/watcher debug`
debugging and maybe cheaty commands, some may be undocumented
- `boogeychance <CHANCE>` : sets `sequentialBoogeyChance` to `<CHANCE>` ( does not save it )
