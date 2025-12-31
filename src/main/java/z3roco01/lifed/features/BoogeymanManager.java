package z3roco01.lifed.features;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.util.ChatUtil;
import z3roco01.lifed.util.PlayerUtil;
import z3roco01.lifed.util.TaskScheduling;
import z3roco01.lifed.util.TitleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BoogeymanManager {
    private static final ArrayList<ServerPlayerEntity> boogeymen = new ArrayList<>();

    public static List<ServerPlayerEntity> getBoogeymen() {
        // creates an immutable list of the boogeymen
        return List.of(boogeymen.toArray(new ServerPlayerEntity[] {}));
    }

    /**
     * Removes all boogeys
     */
    public static void clearBoogeymen() {
        boogeymen.clear();
    }

    /**
     * Cures a player who is a boogey, removing them from the list, and giving them a few status effects
     * @param player the player to cure
     */
    public static void cure(ServerPlayerEntity player) {
        // only cure if they are actually a boogey
        if(!boogeymen.contains(player)) return;

        // remove them from the list
        boogeymen.remove(player);

        // show them the title, and a sound
        TitleUtil.sendTitle(player, "You are cured !", Formatting.GREEN);
        player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1, 1);

        // give them 10 seconds of regen and resistance
        PlayerUtil.addStatusEffect(player, StatusEffects.REGENERATION, 10);
        PlayerUtil.addStatusEffect(player, StatusEffects.RESISTANCE, 10);
    }

    /**
     * Fails all remaining boogeys, called at the end of a session
     */
    public static void failAll() {
        for(ServerPlayerEntity boogey : boogeymen)
            fail(boogey);
    }

    /**
     * fails one player, setting them to red life
     * @param player the player to fail
     */
    public static void fail(ServerPlayerEntity player) {
        // dont fail a non boogey
        if(!boogeymen.contains(player)) return;

        // remove the player from the boogeys
        boogeymen.remove(player);

        // set the player down to 1 life
        LifeManager.setLives(player, 1);

        // send them a fail message
        TitleUtil.sendTitle(player, "You failed...", Formatting.RED);
    }


    /**
     * Rolls a random amount of boogeys between 1 and max
     * each boogey is half as possible as the last
     * @param max the max amount of boogeys possible
     */
    public static void rollBoogeys(int max) {
        // will be at least one
        int boogeys = 0;

        Random random = new Random();
        // did the last roll succeed
        boolean succeeded = true;
        // decimal percent chance that the next boogey will be chosen
        double chance = 1;

        while(succeeded && boogeys < max) {
            // add a new boogey
            boogeys++;
            // half the chance
            chance /= 2;

            // if it is successful, then the next will be chosen
            succeeded = (random.nextDouble() > chance);
        }

        startBoogeymanChosing(boogeys);
    }

    /**
     * Chooses a specified amount of boogeymen, starts the 5 minute timer
     * @param max the amount
     */
    public static void startBoogeymanChosing(int max) {
        String boogeyText = switch(max) {
            case 1 -> "boogeyman";
            default -> "boogeymen";
        };

        ChatUtil.sendChatMessage("The " + boogeyText + " will be chosen in 5 minutes...", Formatting.RED);
        TaskScheduling.scheduleTask(4800, () -> {
            ChatUtil.sendChatMessage("The " + boogeyText + " will be chosen in 1 minute...", Formatting.RED);
            TaskScheduling.scheduleTask(1100, () -> {
                ChatUtil.sendChatMessage("The " + boogeyText + " will be chosen soon.....", Formatting.RED);
                TaskScheduling.scheduleTask(100, () -> {
                    // clear them just before selecting
                    clearBoogeymen();

                    selectBoogeys(max);

                    List<ServerPlayerEntity> players = Lifed.SERVER.getPlayerManager().getPlayerList();
                    showBoogeyStatus(players);
                });
            });
        });
    }

    /**
     * Shows players their boogey status as a title
     * ONLY TO BE CALLED IN A SEPERATE THREAD
     */
    private static void showBoogeyStatus(List<ServerPlayerEntity> players) {
        // show anticipation title
        for(ServerPlayerEntity player : players)
            TitleUtil.sendTitle(player, Lifed.config.youAre, Formatting.YELLOW);

        TaskScheduling.scheduleTask(60, () -> {
            // loop over every player
            for(ServerPlayerEntity player : players) {
                if(boogeymen.contains(player)) {
                    TitleUtil.sendTitle(player, Lifed.config.aBoogeyman, Formatting.RED);
                    player.sendMessage(Text.of(Lifed.config.boogeyChatMsg));
                }else {
                    TitleUtil.sendTitle(player, Lifed.config.notABoogeyman, Formatting.GREEN);
                }
            }
        });
    }

    /**
     * selects up to max boogeys
     * @param max max amount of boogeys, each one is half as likely as the last ( 1st 100% )
     */
    private static void selectBoogeys(int max) {
        List<ServerPlayerEntity> players = Lifed.SERVER.getPlayerManager().getPlayerList();

        // if there is somehow not enough players, just pull everyone
        int realMax = max;
        if(players.size() < max)
            realMax = players.size();

        // do count number of selections
        for(int i = 0; i < realMax; ++i) {
            // if the boogey list size ever equals the player list size, finish
            if(boogeymen.size() == players.size())
                break;

            selectOneBoogey(players);
        }
        
    }

    /**
     * Selects one boogey, and adds them to the list
     * @param players list of all players
     */
    private static void selectOneBoogey(List<ServerPlayerEntity> players) {
        Random random = new Random();

        ServerPlayerEntity boogey = getBoogeyCandidate(random, players);

        if(boogeymen.size() >= players.size())
            return;

        // if the pulled player is already a boogey, re pull
        while(boogeymen.contains(boogey)) {
            boogey = getBoogeyCandidate(random, players);
        }

        // the player will not already be a boogey now, so add them to the list
        boogeymen.add(boogey);
    }

    /**
     * Essentially selects one random person
     * @param random a random objet
     * @param players the players to choose from
     * @return the chosen player
     */
    private static ServerPlayerEntity getBoogeyCandidate(Random random, List<ServerPlayerEntity> players) {
        int boogeyIdx = 0;

        // error happened without this i dont remember
        if(players.size() > 1) {
            boogeyIdx = random.nextInt(players.size());
        }

        return players.get(boogeyIdx);
    }
}
