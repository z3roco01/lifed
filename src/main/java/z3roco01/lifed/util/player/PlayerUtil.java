package z3roco01.lifed.util.player;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerUtil {
    /**
     * Plays the totem animation for a player ( does not do anything else the totem does )
     * @param player the player
     */
    public static void playTotemAnimation(ServerPlayerEntity player) {
        player.getEntityWorld().sendEntityStatus(player, EntityStatuses.USE_TOTEM_OF_UNDYING);
    }

    /**
     * adds a status effect to the player, in seconds
     * @param effect the effect to add
     * @param duration how many SECONDS it will last
     */
    public static void addStatusEffect(ServerPlayerEntity player, RegistryEntry<StatusEffect> effect, int duration) {
        player.addStatusEffect(new StatusEffectInstance(effect, duration*20));
    }
}
