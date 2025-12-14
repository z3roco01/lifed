package z3roco01.lifed.util;

import net.minecraft.scoreboard.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import z3roco01.lifed.Lifed;

public class ScoreboardUtil {
    /**
     * Create a scoreboard objective with the DUMMY criterion
     * @param name the name and display name of the objective
     * @return the newly created objective, or the already existing objective
     */
    public static ScoreboardObjective createObjective(String name) {
        return createObjective(name, ScoreboardCriterion.DUMMY);
    }

    /**
     * Creates a scoreboard with the specified parameters
     * @param name the name and display name of the scoreboard
     * @param criterion the criterion of the scoreboard, also used for the render type
     * @return the newly created objective, or null if ti already exists
     */
    public static ScoreboardObjective createObjective(String name, ScoreboardCriterion criterion) {
        // if there is no server we cant do anything
        if(Lifed.server == null) return null;

        // get a reference to the scoreboard
        Scoreboard scoreboard = Lifed.server.getScoreboard();

        // get the objective if it already exists
        ScoreboardObjective existing = scoreboard.getNullableObjective(name);
        // if it already exists, return the existing objective
        if(existing != null) return existing;

        // create the scoreboard
        return scoreboard.addObjective(name, criterion, Text.of(name), criterion.getDefaultRenderType(),
                false, null);
    }

    /**
     * Set the score to a new score
     * @param objective the objective
     * @param player the player whos score will be changed
     * @param score the new score value
     */
    public static void setScore(ScoreboardObjective objective, ServerPlayerEntity player, int score) {
        // if there is no server we cant do anything
        if(Lifed.server == null) return;

        // get the scoreboard
        Scoreboard scoreboard = Lifed.server.getScoreboard();
        // get the score object, then set the score
        scoreboard.getOrCreateScore(ScoreHolder.fromProfile(player.getGameProfile()), objective).setScore(score);
    }

    /**
     * Get the score of a player
     * @param objective the objective
     * @param player the player whos score is being returned
     * @return the players score, or -100 if there is an error
     */
    public static int getScore(ScoreboardObjective objective, ServerPlayerEntity player) {
        // if there is no server we cant do anything
        if(Lifed.server == null) return -1;

        // get the scoreboard
        Scoreboard scoreboard = Lifed.server.getScoreboard();
        // get a readable score object
        ReadableScoreboardScore score = scoreboard.getScore(ScoreHolder.fromProfile(player.getGameProfile()),
                objective);

        if(score == null) return -1;
        return score.getScore();
    }
}
