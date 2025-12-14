package z3roco01.lifed.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.lifes.LifeHandler;

public class LifedEvents {
    /**
     * Registers all events, like tick and serer events
     */
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(LifedEvents::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPED.register(LifedEvents::onServerStopped);
        ServerPlayConnectionEvents.JOIN.register(LifedEvents::onPlayerJoin);
    }

    /**
     * Called once the server has fulled started, sets the server object on the Lifed object
     * @param server a reference to the server object
     */
    private static void onServerStarted(MinecraftServer server) {
        Lifed.server = server;
        LifeHandler.init();
    }

    /**
     * Called every time a player joins, if needed, adds them to the scoreboard
     * @param handler the handler, contains the player
     * @param sender packet source
     * @param server minecraft server reference
     */
    private static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {

    }

    /**
     * Called once the server has been closed, sets the server object to null
     * @param server the server that has just closed
     */
    private static void onServerStopped(MinecraftServer server) {
        Lifed.server = null;
    }
}
