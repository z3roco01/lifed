package z3roco01.lifed.config;

import z3roco01.composed.annotation.Comment;
import z3roco01.composed.annotation.ConfigProperty;

public class AutoSetupConfig {
    @Comment(comment = "Should autosetup roll lives on the first session like Last Life")
    @ConfigProperty
    public boolean rollLives = false;


    @Comment(comment = "Should autosetup roll boogeys every session like Last Life")
    @ConfigProperty
    public boolean rollBoogeys = false;

    @Comment(comment = "Should autosetup roll soulmates on the first session")
    @ConfigProperty
    public boolean rollSoulmates = false;
}
