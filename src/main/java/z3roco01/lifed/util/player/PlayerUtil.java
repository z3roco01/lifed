package z3roco01.lifed.util.player;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class PlayerUtil {
    /**
     * Plays the totem animation for a player ( does not do anything else the totem does )
     * @param player the player
     */
    public static void playTotemAnimation(ServerPlayer player) {
        // idk if this actually will work...
        player.level().broadcastEntityEvent(player, (byte)35);
    }

    /**
     * adds a status effect to the player, in seconds
     * @param effect the effect to add
     * @param duration how many SECONDS it will last
     */
    public static void addStatusEffect(ServerPlayer player, MobEffect effect, int duration) {
        player.addEffect(new MobEffectInstance(Holder.direct(effect), duration*20));
    }
}
