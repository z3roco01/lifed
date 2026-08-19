package z3roco01.lifed.config;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import z3roco01.composed.annotation.Comment;
import z3roco01.composed.annotation.ConfigProperty;

import java.util.ArrayList;

public class GameplayConfig {
    public GameplayConfig() {
        // add default values, not required but niced
        bannedItems.add(net.minecraft.world.item.Items.BOOKSHELF);

        uncraftableItems.add(Items.ENCHANTING_TABLE);

        bannedEffects.add(MobEffects.STRENGTH.value());
    }

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
    public ArrayList<MobEffect> bannedEffects = new ArrayList<>();

    @Comment(comment = "Are PVP enchantments ( sharpness, protection, etc ) allowed at levels higher than 1")
    @ConfigProperty
    public boolean highLevelPvpEnchAllowed = false;

    @Comment(comment = "Are non-PVP enchantments ( unbreaking, fortune, etc ) allowed at levels higher than 1")
    @ConfigProperty
    public boolean highLevelOtherEnchAllowed = true;

    @Comment(comment = "Is mending banned for applying or rolling")
    @ConfigProperty
    public boolean mendingBanned = true;

    @Comment(comment = "Allow wolves to spawn in more biomes ( only flower forests and birch forests right now )")
    @ConfigProperty
    public boolean expandedWolfSpawning = true;

    @Comment(comment = "Can totems be right clicked to add to the players lives")
    @ConfigProperty
    public boolean totemsConvertable = true;

    @Comment(comment = "Max amount of wolves a player can get ( includes from breading ), set to -1 to disable")
    @ConfigProperty
    public int wolfLimit = 5;

    @Comment(comment = "Should debug watcher commands be enabled")
    @ConfigProperty
    public boolean watcherDebug = false;

    @Comment(comment = "Should events like life gives and boogey deaths be sent to the server console")
    @ConfigProperty
    public boolean logEvents = false;

    @Comment(comment = "Default world border size to set it to (if lower than 1 it won't be set)")
    @ConfigProperty
    public int borderSize = 500;
}
