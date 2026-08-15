package z3roco01.lifed.features;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.util.Time;
import z3roco01.lifed.util.player.ChatUtil;
import z3roco01.lifed.util.player.PlayerUtil;
import z3roco01.lifed.util.TaskScheduling;
import z3roco01.lifed.util.player.TitleUtil;

import java.util.*;

public class BoogeymanManager {
    private static final ArrayList<UUID> boogeymen = new ArrayList<>();

    /**
     * Creates a new list of all the boogey players, does not allow modification
     */
    public static List<UUID> getBoogeymen() {
        // creates an immutable list of the boogeymen
        return Collections.unmodifiableList(boogeymen);
    }

    /**
     * Removes all boogeys
     */
    public static void clearBoogeymen() {
        SessionLock.unlock();

        boogeymen.clear();
    }

    private static boolean contains(ServerPlayer player) {
        return boogeymen.contains(player.getUUID());
    }

    private static void remove(ServerPlayer player) {
        boogeymen.remove(player.getUUID());
    }

    public static void add(ServerPlayer player) {
        boogeymen.add(player.getUUID());
    }

    /**
     * Cures a player who is a boogey, removing them from the list, and giving them a few status effects
     * @param player the player to cure
     */
    public static void cure(ServerPlayer player) {
        // only cure if they are actually a boogey
        if(!contains(player)) return;

        // remove them from the list
        remove(player);

        // show them the title, and a sound
        TitleUtil.sendTitle(player, "You are cured !", ChatFormatting.GREEN);
        player.playSound(SoundEvents.PLAYER_LEVELUP, 1, 1);

        // give them 10 seconds of regen and resistance
        PlayerUtil.addStatusEffect(player, MobEffects.REGENERATION.value(), 10);
        PlayerUtil.addStatusEffect(player, MobEffects.RESISTANCE.value(), 10);

        if(Lifed.config.logEvents)
            Lifed.LOGGER.info(player.getPlainTextName() + " has been cured");
    }

    /**
     * Returns true if the player is a boogey
     */
    public static boolean isPlayerBoogey(ServerPlayer player) {
        return boogeymen.contains(player.getUUID());
    }

    /**
     * Fails all remaining boogeys, called at the end of a session
     */
    public static void failAll() {
        for(ServerPlayer player : Lifed.server.getPlayerList().getPlayers()) {
            if(contains(player))
                fail(player);
        }
        SessionLock.unlock();
    }

    /**
     * fails one player, setting them to red life
     * @param player the player to fail
     */
    public static void fail(ServerPlayer player) {
        // dont fail a non boogey
        if(!contains(player)) return;

        // remove the player from the boogeys
        remove(player);

        // set the player down to 1 life
        LifeManager.setLives(player, 1);

        // send them a fail message
        TitleUtil.sendTitle(player, "You failed...", ChatFormatting.RED);
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
            chance *= Lifed.config.sequentialBoogeyChange;

            // if it is successful, then the next will be chosen
            succeeded = (random.nextDouble() < chance);
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

        // clear them just before selecting
        failAll();

        selectBoogeys(max);

        ChatUtil.sendChatMessage("The " + boogeyText + " will be chosen in 5 minutes...", ChatFormatting.RED);
        TaskScheduling.scheduleTask(Time.MINUTES.ticks(4), () -> {
            ChatUtil.sendChatMessage("The " + boogeyText + " will be chosen in 1 minute...", ChatFormatting.RED);
            TaskScheduling.scheduleTask(Time.SECONDS.ticks(55), () -> {
                ChatUtil.sendChatMessage("The " + boogeyText + " will be chosen soon.....", ChatFormatting.RED);
                TaskScheduling.scheduleTask(Time.SECONDS.ticks(5), () -> {
                    List<ServerPlayer> players = Lifed.server.getPlayerList().getPlayers();
                    showBoogeyStatus(players);
                });
            });
        });
    }

    /**
     * Shows players their boogey status as a title
     */
    public static void showBoogeyStatus(List<ServerPlayer> players) {
        // show anticipation title
        for(ServerPlayer player : players)
            TitleUtil.sendTitle(player, Component.translatable("lifed.you_are").getString(), ChatFormatting.YELLOW);

        TaskScheduling.scheduleTask(Time.SECONDS.ticks(5), () -> {
            // loop over every player
            for(ServerPlayer player : players) {
                if(contains(player)) {
                    TitleUtil.sendTitle(player, Component.translatable("lifed.yes_boogey").getString(), ChatFormatting.RED);
                    // ensure its translated, because client may not have the mod
                    player.sendSystemMessage(Component.literal(Component.translatable("lifed.explain_boogey").getString()));
                }else {
                    TitleUtil.sendTitle(player, Component.translatable("lifed.not_boogey").getString(), ChatFormatting.GREEN);
                }
            }
        });
    }

    /**
     * selects up to max boogeys
     * @param max max amount of boogeys, each one is half as likely as the last ( 1st 100% )
     */
    public static void selectBoogeys(int max) {
        // copy over the list
        ArrayList<ServerPlayer> players = new ArrayList<>(Lifed.server.getPlayerList().getPlayers());
        // weed out red players so people will only actually be boogeys
        players.removeIf(player -> LifeManager.getLives(player) <= 1);

        // cant roll when theres no one
        if(players.isEmpty())
            return;

        // if there is somehow not enough players, just pull everyone
        int realMax = Math.min(players.size(), max);

        // do count number of selections
        for(int i = 0; i < realMax; ++i) {
            // if the boogey list size ever equals the player list size, finish
            if(boogeymen.size() == players.size())
                break;

            selectOneBoogey(players);
        }

        if(Lifed.config.lockoutPlayers)
            SessionLock.lock("Boogeys have been chosen, new players are not allowed in. Wait till next time or contact an admin.");
    }

    /**
     * Selects one boogey, and adds them to the list
     * @param players list of all players
     */
    private static void selectOneBoogey(List<ServerPlayer> players) {
        Random random = new Random();

        ServerPlayer boogey = getBoogeyCandidate(random, players);

        if(boogeymen.size() >= players.size())
            return;

        // if the pulled player is already a boogey, re pull
        while(contains(boogey)) {
            boogey = getBoogeyCandidate(random, players);
        }

        // the player will not already be a boogey now, so add them to the list
        add(boogey);
    }

    /**
     * Essentially selects one random person
     * @param random a random objet
     * @param players the players to choose from
     * @return the chosen player
     */
    private static ServerPlayer getBoogeyCandidate(Random random, List<ServerPlayer> players) {
        int boogeyIdx = 0;

        // error happened without this i dont remember
        if(players.size() > 1) {
            boogeyIdx = random.nextInt(players.size());
        }

        return players.get(boogeyIdx);
    }

    /**
     * Gets around mixins not being referncable, hold previous boogey status
     */
    public interface PotentialBoogey {
        boolean getPreviousBoogey();
    }
}
