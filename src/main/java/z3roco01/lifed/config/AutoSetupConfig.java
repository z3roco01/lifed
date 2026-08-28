package z3roco01.lifed.config;

import z3roco01.composed.annotation.Comment;
import z3roco01.composed.annotation.ConfigProperty;

public class AutoSetupConfig {
    @Comment(comment = "Should autosetup roll lives on the first session like Last Life")
    @ConfigProperty
    public boolean rollLives = false;

    @Comment(comment = "Max lives for the roll, if enabled")
    @ConfigProperty
    public int rollMax = 6;

    @Comment(comment = "Minimum lives for the roll, if enabled")
    @ConfigProperty
    public int rollMin = 2;

    @Comment(comment = "Should autosetup roll boogeys every session like Last Life")
    @ConfigProperty
    public boolean rollBoogeys = false;

    @Comment(comment = "Should autosetup roll soulmates on the first session")
    @ConfigProperty
    public boolean rollSoulmates = false;

    @Comment(comment = "How many lives should each player be set to on the first session, set <0 to disable")
    @ConfigProperty
    public int startingLives = -1;

    @Comment(comment = "Default world border size to set it to (if lower than 1 it won't be set)")
    @ConfigProperty
    public int borderSize = 500;
}
