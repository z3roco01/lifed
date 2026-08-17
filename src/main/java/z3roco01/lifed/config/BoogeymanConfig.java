package z3roco01.lifed.config;

import z3roco01.composed.annotation.Comment;
import z3roco01.composed.annotation.ConfigProperty;

public class BoogeymanConfig {
    @Comment(comment = "The maximum amount of boogeymen on a normal roll ( can be overriden in the command as well )")
    @ConfigProperty
    public int maxBoogeymen = 10;

    @Comment(comment = "When true, does not allow players to join after boogeys have been rolled, since that is kinda cheating")
    @ConfigProperty
    public boolean lockoutPlayers = true;

    @Comment(comment = "How likely is it for the next sequential boogey to be chosen, first is 100%\n# for example, on 75% ( 0.75 ) 1st boogey 100%, 2nd 75%, 3rd 56.25%, 4th 42.19%")
    @ConfigProperty
    public float sequentialBoogeyChange = 0.5f;
}
