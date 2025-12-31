package z3roco01.lifed.features;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.GameMode;
import org.jspecify.annotations.Nullable;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.util.*;

import java.util.Collection;
import java.util.Random;

/**
 * Handles the scoreboards to keep track of lives
 */
public class LifeManager {
    @Nullable
    public static ScoreboardObjective LIVES_OBJECTIVE = null;
    public static Team FOUR_PLUS_TEAM = null;
    public static Team THREE_TEAM = null;
    public static Team TWO_TEAM = null;
    public static Team ONE_TEAM = null;
    public static Team ZERO_TEAM = null;

    public static void init() {
        initializeScoreboard();
        initializeTeams();
    }

    /**
     * Creates the scoreboard that keeps track of lives
     */
    private static void initializeScoreboard() {
        // if there is no server we cant do anything
        if(Lifed.SERVER == null) return;

        // create an objective to keep track of lives
        LIVES_OBJECTIVE = ScoreboardUtil.createObjective("lives");
    }

    /**
     * Create the teams for each life group
     */
    private static void initializeTeams() {
        FOUR_PLUS_TEAM = TeamUtil.createTeam("four_lives", Formatting.DARK_GREEN);
        THREE_TEAM = TeamUtil.createTeam("three_lives", Formatting.GREEN);
        TWO_TEAM = TeamUtil.createTeam("two_lives", Formatting.YELLOW);
        ONE_TEAM = TeamUtil.createTeam("one_life", Formatting.RED);
        ZERO_TEAM = TeamUtil.createTeam("zero_lives", Formatting.GRAY);
    }

    /**
     * Handles the de-initialization of things like teams
     */
    public static void fini() {
        finiTeams();
    }

    private static void finiTeams() {
        TeamUtil.removeTeam(FOUR_PLUS_TEAM);
        TeamUtil.removeTeam(THREE_TEAM);
        TeamUtil.removeTeam(TWO_TEAM);
        TeamUtil.removeTeam(ONE_TEAM);
        TeamUtil.removeTeam(ZERO_TEAM);
    }

    /**
     * Increments the players lives by one
     * @param player the player to add a life to
     */
    public static void addLife(ServerPlayerEntity player) {
       int curLives = getLives(player);
       setLives(player, curLives+1);

       // play level up indicator everytime someone is given a life
       player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1, 1);

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
        return ScoreboardUtil.getScore(LIVES_OBJECTIVE, player);
    }

    /**
     * Sets a players current lives to a new value
     * @param player the player
     * @param lives the new life count for the player
     */
    public static void setLives(ServerPlayerEntity player, int lives) {
        LoggingUtil.log(player.getStringifiedName() + " is now at " + lives + " lives !");
        ScoreboardUtil.setScore(LIVES_OBJECTIVE, player, lives);
        updateTeam(player);
    }

    /**
     * lookups the colour for the passed amount of lives
     * @param lives the current number of lives
     * @return the Formatting colour of the lives
     */
    public static Formatting getLifeColour(int lives) {
        Formatting colour = Formatting.GRAY;

        if(lives > 0) {
            colour = switch (lives) {
                case 1 -> Formatting.RED;
                case 2 -> Formatting.YELLOW;
                case 3 -> Formatting.GREEN;
                default -> // everything > 3 is dark green ( < 0 is ignored by if statement )
                        Formatting.DARK_GREEN;
            };
        }

        return colour;
    }

    /**
     * Gets the correct team for the player based on their lives
     * @param player the player
     * @return the Team object for the players current lives
     */
    public static Team getTeam(ServerPlayerEntity player) {
        int lives = LifeManager.getLives(player);

        // if it is < 0 return zero lives team, so that the default case of lives will trigger >= 4
        if(lives <= 0)
            return ZERO_TEAM;
        return switch (lives) {
            case 1 -> ONE_TEAM;
            case 2 -> TWO_TEAM;
            case 3 -> THREE_TEAM;
            default -> FOUR_PLUS_TEAM;
        };
    }

    /**
     * Gets the current life colour of a player
     * @param player the player to look up
     * @return the colour in Formatting
     */
    public static Formatting getLifeColour(ServerPlayerEntity player) {
        int curLives = getLives(player);
        return getLifeColour(curLives);
    }

    /**
     * Handles the gifting of a life from one person to another
     * @param gifter the person who the life is taken from
     * @param recipient the person who gets the life
     * @return true if the life was successfully given, false otherwise
     */
    public static boolean giftLife(ServerPlayerEntity gifter, ServerPlayerEntity recipient) {

        if(gifter == recipient)
            return false;

        int curLives = LifeManager.getLives(gifter);

        // if the gifter has 1 or less lives, dont let them gift
        if(curLives <= 1)
            return false;
        // cannot resurect
        if(LifeManager.getLives(recipient) < 1)
            return false;

        LifeManager.removeLife(gifter);
        LifeManager.addLife(recipient);

        // show titles informing each person what happened
        TitleUtil.sendTitle(gifter, "You gifted " + recipient.getStringifiedName() + " a life !",
                LifeManager.getLifeColour(gifter));
        TitleUtil.sendTitle(recipient, gifter.getStringifiedName() + " gifted you a life !",
                LifeManager.getLifeColour(recipient));

        recipient.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP);
        PlayerUtil.playTotemAnimation(gifter);

        return true;
    }

    /**
     * Randomizes the lives of the players in the list from 2 to 6 lives
     * @param players collection of players being randomized
     */
    public static void randomizePlayers(Collection<ServerPlayerEntity> players) {
        randomizePlayers(players, 2, 6);
    }

    /**
     * Randomize the lives of passed players between min and max ( inclusive
     * @param players the players to randomize
     * @param min the minimum amount of lives
     * @param max the maximum amount of lives
     */
    public static void randomizePlayers(Collection<ServerPlayerEntity> players, int min, int max) {
        // cant execute without a server
        if(Lifed.SERVER == null) return;

        // create a random object, to get random life values, should maybe use world random but wtv
        Random random = new Random();

        // for each player, give them a random amount of lives
        for (ServerPlayerEntity player : players) {
            // +2 since the nextInt function is 0 to arg inclusive
            int lives = random.nextInt(min, max+1);
            LifeManager.setLives(player, lives);

            // show them how many they got
            TitleUtil.sendTitle(player, "You get " + lives + " lives !", LifeManager.getLifeColour(lives));
        }
    }

    /**
     * Puts the passed player in the correct team for their life count
     * @param player the player to update
     */
    public static void updateTeam(ServerPlayerEntity player) {
        TeamUtil.addPlayerToTeam(player, getTeam(player));
    }
}
