package z3roco01.lifed.config;

import z3roco01.composed.annotation.Comment;
import z3roco01.composed.annotation.ConfigProperty;

public class SessionConfig {
    @Comment(comment = "How long a session goes for, players will be frozen when the timer runs out ")
    @ConfigProperty
    public int sessionLength = 180;

    @Comment(comment = "The length of breaks when the break command is used")
    @ConfigProperty
    public int breakLength = 10;

    @Comment(comment = "Enables the session time when the server starts, pausing players and the world as well")
    @ConfigProperty
    public boolean startSessionTimer = false;
}
