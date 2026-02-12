package z3roco01.lifed.config;


import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import z3roco01.composed.ProcessedConfig;
import z3roco01.composed.annotation.Comment;
import z3roco01.composed.annotation.ConfigProperty;
import z3roco01.lifed.Lifed;

import java.util.ArrayList;

/**
 * Handles config properties, loaded with the Composed library
 */
public class LifedConfig implements ProcessedConfig {
    public LifedConfig() {
        // add default values, not required but niced
        bannedItems.add(Items.BOOKSHELF);

        uncraftableItems.add(Items.ENCHANTING_TABLE);

        bannedEffects.add(StatusEffects.STRENGTH.value());
    }

    @Override
    public void process() {
        Lifed.LOGGER.info(bannedItems.toString());
        Lifed.LOGGER.info(uncraftableItems.toString());
        Lifed.LOGGER.info(bannedEffects.toString());
    }

    @Comment(comment = "The maximum amount of boogeymen on a normal roll ( can be overriden in the command as well )")
    @ConfigProperty
    public int maxBoogeymen = 10;

    @Comment(comment = "The title shown just before the players boogey status is shown")
    @ConfigProperty
    public String youAre = "You are...";

    @Comment(comment = "The title shown when the player is not chosen as boogey")
    @ConfigProperty
    public String notABoogeyman = "...NOT a boogeyman !!!";

    @Comment(comment = "The title shown when the player is chosen as boogey")
    @ConfigProperty
    public String aBoogeyman = "...A BOOGEYMAN !!!";

    @Comment(comment = "The chat message sent only to boogeys explaining the rules of boogeys")
    @ConfigProperty
    public String boogeyChatMsg ="§7You are a boogeyman ! you must kill a §2dark green§7, §agreen§7 or §eyellow§7 to cure yourself. you lose EVERY alliance as a boogeyman until you are cured. if you do not then you will go to your §clast life§7 at the end of this session.§r";

    @Comment(comment = "how many lightning bolts to be spawned on red deaht ( they all happen at once so more than one is kinda pointless )")
    @ConfigProperty
    public int lightningsOnRedDeath = 5;

    @Comment(comment = "Items which are completely banned, they cannot be crafted or picked up, if they are picked up the item will disappear ( contains their ids )")
    @ConfigProperty
    public ArrayList<Item> bannedItems = new ArrayList<>();

    @Comment(comment = "Items which canont be crafted, but can be obtained ( like the enchanter in last life ), contains their ids")
    @ConfigProperty
    public ArrayList<Item> uncraftableItems = new ArrayList<>();

    @Comment(comment = "Status effects which cannot be applied to players, the potions can be made, but once drank will have no effect ( contains their ids )")
    @ConfigProperty
    public ArrayList<StatusEffect> bannedEffects = new ArrayList<>();

    @Comment(comment = "Are PVP enchantments ( sharpness, protection, etc ) allowed at levels higher than 1")
    @ConfigProperty
    public boolean highLevelPvpEnchAllowed = false;

    @Comment(comment = "Are non-PVP enchantments ( unbreaking, fortune, etc ) allowed at levels higher than 1")
    @ConfigProperty
    public boolean highLevelOtherEnchAllowed = true;

    @Comment(comment = "Allow wolves to spawn in more biomes ( only flower forests and birch forests right now )")
    @ConfigProperty
    public boolean expandedWolfSpawning = true;

    @Comment(comment = "How long a session goes for, players will be frozen when the timer runs out ")
    @ConfigProperty
    public int sessionLength = 180;

    @Comment(comment = "The length of breaks when the break command is used")
    @ConfigProperty
    public int breakLength = 10;

    @Comment(comment = "Enables the session time when the server starts, pausing players and the world as well")
    @ConfigProperty
    public boolean startSessionTimer = false;

    @Comment(comment = "When true, does not allow players to join after boogeys have been rolled, since that is kinda cheating")
    @ConfigProperty
    public boolean lockoutPlayers = true;

    @Comment(comment = "Can totems be right clicked to add to the players lives")
    @ConfigProperty
    public boolean totemsConvertable = true;

    @Comment(comment = "Max amount of wolves a player can get ( includes from breading ), set to -1 to disable")
    @ConfigProperty
    public int wolfLimit = 5;

    @Comment(comment = "How likely is it for the next sequential boogey to be chosen, first is 100%\n# for example, on 75% ( 0.75 ) 1st boogey 100%, 2nd 75%, 3rd 56.25%, 4th 42.19%")
    @ConfigProperty
    public float sequentialBoogeyChange = 0.5f;

    @Comment(comment = "Should debug watcher commands be enabled")
    @ConfigProperty
    public boolean watcherDebug = false;

    @Comment(comment = "Should events like life gives and boogey deaths be sent to the server console")
    @ConfigProperty
    public boolean logEvents = false;
}