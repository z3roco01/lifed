package z3roco01.lifed.lifes;

import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.jspecify.annotations.Nullable;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.util.LoggingUtil;
import z3roco01.lifed.util.ScoreboardUtil;

/**
 * Handles the scoreboards to keep track of lives
 */
public class LifeHandler {
    @Nullable
    public static ScoreboardObjective livesObjective = null;

    public static void init() {
        initializeScoreboard();
    }

    /**
     * Creates the scoreboard that keeps track of lives
     */
    private static void initializeScoreboard() {
        // if there is no server we cant do anything
        if(Lifed.server == null) return;

        // create an objective to keep track of lives
        livesObjective = ScoreboardUtil.createObjective("lives");
        Lifed.LOGGER.info("asd " + (livesObjective == null));
    }

    /**
     * Increments the players lives by one
     * @param player the player to add a life to
     */
    public static void addLife(ServerPlayerEntity player) {
       int curLives = getLives(player);
       setLives(player, curLives+1);

       // if the player has gone from 0 lives to above 0, set them to survival
       if(curLives <= 0 && getLives(player) > 0)
           player.changeGameMode(GameMode.SURVIVAL);
    }

    /**
     * Decrements a players lives by one, and puts a player into spectator if <=0 lives
     * @param player the player to decrement from
     */
    public static void removeLife(ServerPlayerEntity player) {
        int curLives = getLives(player);
        setLives(player, curLives-1);

        // if the player is too low on lives, put them in spectator
        if(getLives(player) <= 0)
            player.changeGameMode(GameMode.SPECTATOR);
    }

    /**
     * Gets how many lives a player currently has
     * @param player the player
     * @return the amount of lives
     */
    public static int getLives(ServerPlayerEntity player) {
        return ScoreboardUtil.getScore(livesObjective, player);
    }

    /**
     * Sets a players current lives to a new value
     * @param player the player
     * @param lives the new life count for the player
     */
    public static void setLives(ServerPlayerEntity player, int lives) {
        LoggingUtil.log(player.getStringifiedName() + " is now at " + lives + "lives !");
        ScoreboardUtil.setScore(livesObjective, player, lives);
    }
}
