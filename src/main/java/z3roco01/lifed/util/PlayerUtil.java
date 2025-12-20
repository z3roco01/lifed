package z3roco01.lifed.util;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerUtil {
    /**
     * Plays the totem animation for a player ( does not do anything else the totem does )
     * @param player the player
     */
    public static void playTotemAnimation(ServerPlayerEntity player) {
        player.getEntityWorld().sendEntityStatus(player, EntityStatuses.USE_TOTEM_OF_UNDYING);
    }
}
