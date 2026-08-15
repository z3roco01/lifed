package z3roco01.lifed.util;

import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * Holds a UUID that is generated on server startup, and deleted on server shut down
 */
public class SessionUUID {
    private static @Nullable UUID uuid = null;

    public static @Nullable UUID getCurrentUuid() {
        return uuid;
    }

    /**
     * Generates the uuid, registered as an event
     */
    public static void onServerStart(MinecraftServer server) {
        uuid = UUID.randomUUID();
    }

    /**
     * Nulls the UUID once the server has stopped
     */
    public static void onServerStopped(MinecraftServer server) {
        uuid = null;
    }
}
