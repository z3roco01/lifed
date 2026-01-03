package z3roco01.lifed.config;


import z3roco01.composed.annotation.Comment;
import z3roco01.composed.annotation.ConfigProperty;

public class LifedConfig {
    @Comment(comment = "The maximum amount of boogeymen on a normal roll ( can be overriden in the command as well )")
    @ConfigProperty
    public Integer maxBoogeymen = 10;

    @Comment(comment = "The title shown just before the players boogey status is shown")
    @ConfigProperty
    public String youAre = "You are...";

    @Comment(comment = "The title shown when the player is not chosen as boogey")
    @ConfigProperty
    public String notABoogeyman = "...NOT a boogeyman !!!";

    @Comment(comment = "The title shown when the player is chosen as boogey")
    @ConfigProperty
    public String aBoogeyman = "...A BOOGEYMAN";

    @Comment(comment = "The chat message sent only to boogeys explaining the rules of boogeys")
    @ConfigProperty
    public String boogeyChatMsg ="§7You are a boogeyman ! you must kill a §2dark green§7, §agreen§7 or §eyellow§7 to cure yourself. you lose EVERY alliance as a boogeyman until you are cured. if you do not then you will go to your §clast life§7 at the end of this session.§r";

    @Comment(comment = "how many lightning bolts to be spawned on red deaht ( they all happen at once so more than one is kinda pointless )")
    @ConfigProperty
    public Integer lightningsOnRedDeath = 5;

    // TODO: Convert banned things to lists instead of toggles
    @ConfigProperty
    public Boolean bookshelfAllowed = false;

    @ConfigProperty
    public Boolean canCraftEnchanter = false;

    @ConfigProperty
    public Boolean strengthAllowed = false;

    @Comment(comment = "Are PVP enchantments ( sharpness, protection, etc ) allowed at levels higher than 1")
    @ConfigProperty
    public Boolean highLevelPvpEnchAllowed = false;

    @Comment(comment = "Are non-PVP enchantments ( unbreaking, fortune, etc ) allowed at levels higher than 1")
    @ConfigProperty
    public Boolean highLevelOtherEnchAllowed = true;

    @Comment(comment = "Allow wovles to spawn in more biomes ( only flower forests and birch forests right now )")
    @ConfigProperty
    public Boolean expandedWolfSpawning = true;

    @Comment(comment = "How long a session goes for, players will be frozen when the timer runs out ")
    @ConfigProperty
    public Integer sessionLength = 180;

    @Comment(comment = "The length of breaks when the break command is used")
    @ConfigProperty
    public Integer breakLength = 10;

    @Comment(comment = "Enables the session time when the server starts, pausing players and the world as well")
    @ConfigProperty
    public Boolean startSessionTimer = false;

    @Comment(comment = "When true, does not allow players to join after boogeys have been rolled, since that is kinda cheating")
    @ConfigProperty
    public Boolean lockoutPlayers = true;

    @Comment(comment = "Can totems be right clicked to add to the players lives")
    @ConfigProperty
    public Boolean totemsConvertable = true;

    @Comment(comment = "Max amount of wolves a player can get ( includes from breading ), set to -1 to disable")
    @ConfigProperty
    public Integer wolfLimit = 5;
}