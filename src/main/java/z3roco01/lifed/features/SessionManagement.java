package z3roco01.lifed.features;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jspecify.annotations.Nullable;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.util.LoggingUtil;
import z3roco01.lifed.util.Time;
import z3roco01.lifed.util.player.ChatUtil;
import z3roco01.lifed.util.player.TitleUtil;

import java.util.ArrayList;

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

    private static final ArrayList<SessionTickEvent> tickEvents = new ArrayList<>();

    // modifiers for freezing players
    private static final AttributeModifier MODIFIER_FREEZE = new AttributeModifier(
            Identifier.fromNamespaceAndPath(Lifed.MOD_ID, "freeze"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    /**
     * Registers the passed tick event
     */
    public static void registerTickEvent(SessionTickEvent tickEvent) {
        tickEvents.add(tickEvent);
    }

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
        // register break end warnings
        registerTickEvent(new SessionTickOneshot(Time.MINUTES.ticks(5),
                () -> ChatUtil.sendChatMessage("5 Minutes remain", ChatFormatting.RED), true));
        registerTickEvent(new SessionTickOneshot(Time.MINUTES.ticks(1),
                () -> TitleUtil.sendTitleAndChat("1 Minute remains", ChatFormatting.RED), true));
        registerTickEvent(new SessionTickOneshot(Time.SECONDS.ticks(30),
                () -> TitleUtil.sendTitleAndChat("30 Seconds remain...", ChatFormatting.RED), true));

        // register session end warnings
        registerTickEvent(new SessionTickOneshot(Time.HOURS.ticks(1),
                () -> TitleUtil.sendTitleAndChat("1 Hour remains...", ChatFormatting.GREEN), false));
        registerTickEvent(new SessionTickOneshot(Time.MINUTES.ticks(30),
                () -> TitleUtil.sendTitleAndChat("30 Minutes remain...", ChatFormatting.YELLOW), false));
        registerTickEvent(new SessionTickOneshot(Time.MINUTES.ticks(15),
                () -> TitleUtil.sendTitleAndChat("15 Minutes remain...", ChatFormatting.YELLOW), false));
        registerTickEvent(new SessionTickOneshot(Time.MINUTES.ticks(5),
                () -> TitleUtil.sendTitleAndChat("5 Minutes remain...", ChatFormatting.RED), false));
        registerTickEvent(new SessionTickOneshot(Time.MINUTES.ticks(1),
                () -> TitleUtil.sendTitleAndChat("1 Minute remains...", ChatFormatting.RED), false));

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
            }else {
                for(SessionTickEvent tickEvent : tickEvents)
                    tickEvent.tick(ticksRemaining, onBreak);
            }
        });
    }

    public static void pause() {
        freezeTicks();
        paused = true;

        for(ServerPlayer player : Lifed.SERVER.getPlayerList().getPlayers())
            applyPlayerFreeze(player);

        LoggingUtil.log("session paused :(");
    }

    /**
     * Sets a players attributes to effectively freeze them, also give saturation
     */
    public static void applyPlayerFreeze(ServerPlayer player) {
        AttributeInstance movementInst = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance jumpInst = player.getAttribute(Attributes.JUMP_STRENGTH);
        AttributeInstance reachInst = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        AttributeInstance damageInst = player.getAttribute(Attributes.ATTACK_DAMAGE);

        // only apply the freeze modifier if its not already applied, avoids error
        if(!movementInst.hasModifier(MODIFIER_FREEZE.id()))
            movementInst.addPermanentModifier(MODIFIER_FREEZE);
        if(!jumpInst.hasModifier(MODIFIER_FREEZE.id()))
            jumpInst.addPermanentModifier(MODIFIER_FREEZE);
        if(!reachInst.hasModifier(MODIFIER_FREEZE.id()))
            reachInst.addPermanentModifier(MODIFIER_FREEZE);
        if(!damageInst.hasModifier(MODIFIER_FREEZE.id()))
            damageInst.addPermanentModifier(MODIFIER_FREEZE);
    }

    /**
     * Undoes everything done in the freeze method
     */
    public static void removePlayerFreeze(ServerPlayer player) {
        player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(MODIFIER_FREEZE);
        player.getAttribute(Attributes.JUMP_STRENGTH).removeModifier(MODIFIER_FREEZE);
        player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).removeModifier(MODIFIER_FREEZE);
        player.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(MODIFIER_FREEZE);
    }

    /**
     * Handles the freezing/unfreezing of a player who has joined mid freeze or after freeze
     */
    public static void handleFreezing(ServerPlayer player) {
        if(paused || onBreak)
            SessionManagement.applyPlayerFreeze(player);
        else
            SessionManagement.removePlayerFreeze(player); // just incase theyre still frozen, like the left mid pause
    }

    public static void unpause() {
        unfreezeTicks();
        paused = false;

        for(ServerPlayer player : Lifed.SERVER.getPlayerList().getPlayers())
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
        ServerTickRateManager serverTickManager = Lifed.SERVER.tickRateManager();

        if (serverTickManager.isSprinting()) {
            serverTickManager.stopSprinting();
        }

        serverTickManager.setFrozen(true);
    }

    /**
     * does the same stuff as /tick unfreeze
     */
    private static void unfreezeTicks() {
        ServerTickRateManager serverTickManager = Lifed.SERVER.tickRateManager();

        serverTickManager.setFrozen(false);
    }

    /**
     * Starts a break and its timer
     */
    public static void goOnBreak() {
        freezeTicks();
        for(ServerPlayer player : Lifed.SERVER.getPlayerList().getPlayers())
            applyPlayerFreeze(player);

        breakTicksRemaining = breakTicksTotal;
        onBreak = true;

        TitleUtil.sendTitleAll(Lifed.config.breakLength + " minute break started", ChatFormatting.RED);
    }

    /**
     * Stops a break, either mid break or at the end
     */
    public static void goOffBreak() {
        for(ServerPlayer player : Lifed.SERVER.getPlayerList().getPlayers())
            removePlayerFreeze(player);
        unfreezeTicks();

        breakTicksRemaining = 0;
        onBreak = false;
        TitleUtil.sendTitleAll("break ended, good luck !", ChatFormatting.RED);
    }

    public static boolean onBreak() {
        return onBreak;
    }

    public static int remainingBreakTicks() {
        return breakTicksRemaining;
    }

    /**
     * Used to register an event to run on every unpaused tick of a session, including breaks
     */
    @FunctionalInterface
    public interface SessionTickEvent {
        /**
         * Called once an active ( non pause ) session tick
         * @param remainingTicks the remaining ticks in the session or break ( ex: at 1 minute it will be 120 ticks )
         * @param isBreak true when this is being run in a break
         */
        void tick(int remainingTicks, boolean isBreak);
    }

    /**
     * Registers a tick event to run one time, once a certain time has been reached
     */
    public static class SessionTickOneshot implements SessionTickEvent {
        private final int triggerTicks;
        @Nullable
        private final Runnable runnable;
        private final boolean runOnBreak;

        /**
         * @param triggerTicks at how many ticks REMAINING ! should this run
         * @param runnable the thing to run
         * @param runOnBreak if true this timer should run on the break instead of the normal session
         */
        public SessionTickOneshot(int triggerTicks, @Nullable Runnable runnable, boolean runOnBreak) {
            this.runnable = runnable;
            this.triggerTicks = triggerTicks;
            this.runOnBreak = runOnBreak;
        }
        /**
         * Called once the specified time has been reached, here so it can be overriden i guess
         */
        void run() {
            if(runnable != null)
                runnable.run();
        }

        @Override
        public void tick(int remainingTicks, boolean isBreak) {
            // only run if it has reached the proper tick, and the break state is the same
            if(remainingTicks == triggerTicks) {
                if(isBreak == runOnBreak)
                    run();
            }
        }
    }
}
