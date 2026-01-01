package z3roco01.lifed.features;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.util.LoggingUtil;
import z3roco01.lifed.util.TaskScheduling;
import z3roco01.lifed.util.Time;
import z3roco01.lifed.util.player.ChatUtil;
import z3roco01.lifed.util.player.TitleUtil;

/**
 * Handles pre session and post session events, such as stopping players an failing boogeys
 */
public class SessionManagement {
    /**
     * How many ticks are left in the session
     */
    private static int ticksRemaining = 0;

    /**
     * when true the ticks remainging timer will not decrement
     */
    private static boolean paused = false;

    /**
     * Has a break been started
     */
    private static boolean onBreak = false;

    /**
     * How many ticks a break takes in total
     */
    private static int breakTicksTotal = 0;

    private static int breakTicksRemaining = 0;

    // modifiers for freezing players
    private static final EntityAttributeModifier MODIFIER_FREEZE = new EntityAttributeModifier(
            Identifier.of(Lifed.MOD_ID, "freeze"), -1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    /**
     * Sets up the timer, registers the ticker, and pawses everything
     */
    public static void initialize() {
        if(Lifed.config.startSessionTimer) {
            ticksRemaining = Time.MINUTES.ticks(Lifed.config.sessionLength);
            pause();
        }else
            ticksRemaining = -1;

        breakTicksTotal = Time.MINUTES.ticks(Lifed.config.breakLength);

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if(onBreak) {
                breakTicksRemaining--;
                if(breakTicksRemaining == 0)
                    goOffBreak();
                // dont tick main timer when on break
                return;
            }

            // no timer when paused
            if(paused) return;

            ticksRemaining--;

            if(ticksRemaining == 0) {
                // failing boogeymen causes concurency error
                //BoogeymanManager.failAll();
                pause();
            }
        });
    }

    public static void pause() {
        freezeTicks();
        paused = true;

        for(ServerPlayerEntity player : Lifed.SERVER.getPlayerManager().getPlayerList())
            applyPlayerFreeze(player);

        LoggingUtil.log("session paused :(");
    }

    /**
     * Sets a players attributes to effectively freeze them, also give saturation
     */
    public static void applyPlayerFreeze(ServerPlayerEntity player) {
        player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).addPersistentModifier(MODIFIER_FREEZE);
        player.getAttributeInstance(EntityAttributes.JUMP_STRENGTH).addPersistentModifier(MODIFIER_FREEZE);
        player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE).addPersistentModifier(MODIFIER_FREEZE);
    }

    /**
     * Undoes everything done in the freeze method
     */
    public static void removePlayerFreeze(ServerPlayerEntity player) {
        player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).removeModifier(MODIFIER_FREEZE);
        player.getAttributeInstance(EntityAttributes.JUMP_STRENGTH).removeModifier(MODIFIER_FREEZE);
        player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE).removeModifier(MODIFIER_FREEZE);
    }

    /**
     * Handles the freezing/unfreezing of a player who has joined mid freeze or after freeze
     */
    public static void handleFreezing(ServerPlayerEntity player) {
        if(paused || onBreak)
            SessionManagement.applyPlayerFreeze(player);
        else
            SessionManagement.removePlayerFreeze(player); // just incase theyre still frozen, like the left mid pause
    }

    public static void unpause() {
        unfreezeTicks();
        paused = false;

        for(ServerPlayerEntity player : Lifed.SERVER.getPlayerManager().getPlayerList())
            removePlayerFreeze(player);

        LoggingUtil.log("session unpaused !");
    }

    public static boolean isPaused() {
        return paused;
    }

    /**
     * Returns the remaining amount of ticks
     */
    public static int ticksRemaining() {
        return ticksRemaining;
    }

    /**
     * does the same stuff as /tick freeze
     */
    private static void freezeTicks() {
        ServerTickManager serverTickManager = Lifed.SERVER.getTickManager();

        if (serverTickManager.isSprinting()) {
            serverTickManager.stopSprinting();
        }

        serverTickManager.setFrozen(true);
    }

    /**
     * does the same stuff as /tick unfreeze
     */
    private static void unfreezeTicks() {
        ServerTickManager serverTickManager = Lifed.SERVER.getTickManager();

        serverTickManager.setFrozen(false);
    }

    /**
     * Starts a break and its timer
     */
    public static void goOnBreak() {
        freezeTicks();
        for(ServerPlayerEntity player : Lifed.SERVER.getPlayerManager().getPlayerList())
            applyPlayerFreeze(player);

        breakTicksRemaining = breakTicksTotal;
        onBreak = true;

        TitleUtil.sendTitleAll(Lifed.config.breakLength + " minute break started", Formatting.RED);

        // schedule warnings
        if(breakTicksRemaining >= Time.MINUTES.ticks(10)) {
            int ticksMinus5min = breakTicksRemaining - Time.MINUTES.ticks(5);
            TaskScheduling.scheduleTask(ticksMinus5min, () -> {
                ChatUtil.sendChatMessage("5 minutes remaining", Formatting.RED);
            });
        }

        if(breakTicksRemaining > Time.MINUTES.ticks(1)) {
            int ticksMinus1min = breakTicksRemaining - Time.MINUTES.ticks(1);
            TaskScheduling.scheduleTask(ticksMinus1min, () -> {
                ChatUtil.sendChatMessage("1 minute until the break ends", Formatting.RED);

                TitleUtil.sendTitleAll("1 minute remaining", Formatting.RED);
            });
        }
    }

    /**
     * Stops a break, either mid break or at the end
     */
    public static void goOffBreak() {
        for(ServerPlayerEntity player : Lifed.SERVER.getPlayerManager().getPlayerList())
            removePlayerFreeze(player);
        unfreezeTicks();

        breakTicksRemaining = 0;
        onBreak = false;
        TitleUtil.sendTitleAll("break ended, good luck !", Formatting.RED);
    }

    public static boolean onBreak() {
        return onBreak;
    }

    public static int remainingBreakTicks() {
        return breakTicksRemaining;
    }
}
