package z3roco01.lifed.features;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.util.LoggingUtil;
import z3roco01.lifed.util.Time;

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

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // no timer when paused
            if(paused) return;

            ticksRemaining--;

            if(ticksRemaining == 0) {
                // errors....
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
}
