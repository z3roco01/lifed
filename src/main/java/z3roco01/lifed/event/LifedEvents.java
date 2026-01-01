package z3roco01.lifed.event;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.commands.CommandRegisterer;
import z3roco01.lifed.commands.PlayerCommands;
import z3roco01.lifed.commands.WatcherCommands;
import z3roco01.lifed.features.LifeManager;
import z3roco01.lifed.features.SessionManagement;
import z3roco01.lifed.util.TaskScheduling;

public class LifedEvents {
    private static final CommandRegisterer[] COMMANDS = {
            new WatcherCommands(),
            new PlayerCommands()
    };

    /**
     * Registers all events, like tick and serer events
     */
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(LifedEvents::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(LifedEvents::onServerStopping);
        ServerPlayConnectionEvents.JOIN.register(LifedEvents::onPlayerJoin);
        CommandRegistrationCallback.EVENT.register(LifedEvents::onCommandsRegister);
    }

    /**
     * Called once the server has fulled started, sets the server object on the Lifed object
     * @param server a reference to the server object
     */
    private static void onServerStarted(MinecraftServer server) {
        Lifed.SERVER = server;
        LifeManager.init();
        SessionManagement.initialize();
    }

    /**
     * Called every time a player joins, if needed, adds them to the scoreboard
     * @param handler the handler, contains the player
     * @param sender packet source
     * @param server minecraft server reference
     */
    private static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.getPlayer();
        // update everytime they join, since teams are volatile
        LifeManager.updateTeam(player);

        if(SessionManagement.isPaused())
            SessionManagement.applyPlayerFreeze(player);
        else
            SessionManagement.removePlayerFreeze(player); // just incase theyre still frozen, like the left mid pause
    }

    /**
     * Calls all command registers to register their commands
     */
    private static void onCommandsRegister(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment
    ) {
        for(CommandRegisterer registerer : COMMANDS)
            registerer.register(dispatcher, registryAccess, environment);
    }

    /**
     * Called once the server is closing, cleans up stuff
     * @param server the server that has just closed
     */
    private static void onServerStopping(MinecraftServer server) {
        TaskScheduling.cancelTasks();
        LifeManager.fini();

        // clear all freeze effects so an error doesnt happen upon rejoining
        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList())
            SessionManagement.removePlayerFreeze(player);

        Lifed.SERVER = null;
    }
}
