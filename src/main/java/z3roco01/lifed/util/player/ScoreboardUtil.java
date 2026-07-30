package z3roco01.lifed.util.player;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import z3roco01.lifed.Lifed;

public class ScoreboardUtil {
    /**
     * Create a scoreboard objective with the DUMMY criterion
     * @param name the name and display name of the objective
     * @return the newly created objective, or the already existing objective
     */
    public static Objective createObjective(String name) {
        return createObjective(name, ObjectiveCriteria.DUMMY);
    }

    /**
     * Retrieves the Scoreboard from the server
     * @return the Scoreboard of the server
     */
    public static Scoreboard getScoreboard() {
        return Lifed.SERVER.getScoreboard();
    }

    /**
     * Creates a scoreboard with the specified parameters
     * @param name the name and display name of the scoreboard
     * @param criterion the criterion of the scoreboard, also used for the render type
     * @return the newly created objective, or null if ti already exists
     */
    public static Objective createObjective(String name, ObjectiveCriteria criterion) {
        // if there is no server we cant do anything
        if(Lifed.SERVER == null) return null;

        // get a reference to the scoreboard
        Scoreboard scoreboard = getScoreboard();

        // get the objective if it already exists
        Objective existing = scoreboard.getObjective(name);
        // if it already exists, return the existing objective
        if(existing != null) return existing;

        // create the scoreboard
        return scoreboard.addObjective(name, criterion, Component.literal(name), criterion.getDefaultRenderType(),
                false, null);
    }

    /**
     * Retrieves the ScoreHolder for a player
     * @param player the player
     * @return the ScoreHolder for the passed player
     */
    public static ScoreHolder getScoreHolder(ServerPlayer player) {
        return ScoreHolder.fromGameProfile(player.getGameProfile());
    }

    /**
     * Set the score to a new score
     * @param objective the objective
     * @param player the player whos score will be changed
     * @param score the new score value
     */
    public static void setScore(Objective objective, ServerPlayer player, int score) {
        // if there is no server we cant do anything
        if(Lifed.SERVER == null) return;

        Scoreboard scoreboard = getScoreboard();
        // get the score object, then set the score
        scoreboard.getOrCreatePlayerScore(getScoreHolder(player), objective).set(score);
    }

    /**
     * Get the score of a player
     * @param objective the objective
     * @param player the player whos score is being returned
     * @return the players score, or -100 if there is an error
     */
    public static int getScore(Objective objective, ServerPlayer player) {
        // if there is no server we cant do anything
        if(Lifed.SERVER == null) return -1;

        Scoreboard scoreboard = getScoreboard();
        // get a readable score object

        ReadOnlyScoreInfo score = scoreboard.getPlayerScoreInfo(getScoreHolder(player), objective);

        if(score == null) return -1;
        return score.value();
    }
}
