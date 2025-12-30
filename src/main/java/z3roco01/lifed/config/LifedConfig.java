package z3roco01.lifed.config;

import z3roco01.lifed.config.annotation.ConfigProperty;

public class LifedConfig {
    @ConfigProperty
    public Integer maxBoogeymen = 10;

    @ConfigProperty
    public String youAre = "You are...";

    @ConfigProperty
    public String notABoogeyman = "...NOT a boogeyman !!!";

    @ConfigProperty
    public String aBoogeyman = "...A BOOGEYMAN";

    @ConfigProperty
    public String boogeyChatMsg ="§7You are a boogeyman ! you must kill a §2dark green§7, §agreen§7 or §eyellow§7 to cure yourself. you lose EVERY alliance as a boogeyman until you are cured. if you do not then you will go to your §clast life§7 at the end of this session.§r";

    /**
     * How many lightning bolts should be summoned on red life death
     */
    @ConfigProperty
    public Integer lightningOnRedDeath = 5;
}
