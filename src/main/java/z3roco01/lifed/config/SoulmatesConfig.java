package z3roco01.lifed.config;

import z3roco01.composed.annotation.Comment;
import z3roco01.composed.annotation.ConfigProperty;

public class SoulmatesConfig {
    @Comment(comment = "How many lives everyone will start with when using soulmates")
    @ConfigProperty
    public int soulmatesLives = 4;

    @Comment(comment = "Should soulmates be revealed to the pair when rolled")
    @ConfigProperty
    public boolean revealSoulmates = false;
}
