package z3roco01.lifed.config;

import z3roco01.lifed.config.annotation.ConfigProperty;

public class LifedConfig {
    /**
     * Max possible amount of boogeys
     */
    @ConfigProperty
    public Integer maxBoogeymen = 10;

    /**
     * Title sent just before boogey status is revealed
     */
    @ConfigProperty
    public String youAre = "You are...";

    /**
     * Title send after a player was not chosen as boogey
     */
    @ConfigProperty
    public String notABoogeyman = "...NOT a boogeyman !!!";

    /**
     * Title sent afte a boogey was chosen as boogey
     */
    @ConfigProperty
    public String aBoogeyman = "...A BOOGEYMAN";

    /**
     * Chat message sent to a player who just became a boogey
     */
    @ConfigProperty
    public String boogeyChatMsg ="§7You are a boogeyman ! you must kill a §2dark green§7, §agreen§7 or §eyellow§7 to cure yourself. you lose EVERY alliance as a boogeyman until you are cured. if you do not then you will go to your §clast life§7 at the end of this session.§r";

    /**
     * How many lightning bolts should be summoned on red life death
     */
    @ConfigProperty
    public Integer lightningOnRedDeath = 5;

    /**
     * Are bookshelves allowed
     */
    @ConfigProperty
    public Boolean bookshelfAllowed = false;

    /**
     * are enchanters allowed to be crafted
     */
    @ConfigProperty
    public Boolean canCraftEnchanter = false;

    /**
     * is strength allowed
     */
    @ConfigProperty
    public Boolean strengthAllowed = false;
}
