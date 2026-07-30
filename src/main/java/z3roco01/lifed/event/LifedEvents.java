package z3roco01.lifed.event;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.commands.CommandRegisterer;
import z3roco01.lifed.commands.PlayerCommands;
import z3roco01.lifed.commands.WatcherCommands;
import z3roco01.lifed.features.LifeManager;
import z3roco01.lifed.features.SessionLock;
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
        ServerPlayConnectionEvents.INIT.register(SessionLock::handlePlayerJoin);
        ServerPlayConnectionEvents.JOIN.register(LifedEvents::onPlayerJoin);
        CommandRegistrationCallback.EVENT.register(LifedEvents::onCommandsRegister);
    }

    /**
     * Called once the server has fulled started, sets the server object on the Lifed object
     * @param server a reference to the server object
     */
    private static void onServerStarted(MinecraftServer server) {
        Lifed.server = server;
        LifeManager.init();
        SessionManagement.initialize();
    }

    /**
     * Called every time a player joins, if needed, adds them to the scoreboard
     * @param handler the handler, contains the player
     * @param sender packet source
     * @param server minecraft server reference
     */
    private static void onPlayerJoin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {

        ServerPlayer player = handler.getPlayer();
        // update everytime they join, since teams are volatile
        LifeManager.updateTeam(player);

        SessionManagement.handleFreezing(player);
    }

    /**
     * Calls all command registers to register their commands
     */
    private static void onCommandsRegister(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext registryAccess,
            Commands.CommandSelection environment
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
        for(ServerPlayer player : server.getPlayerList().getPlayers())
            SessionManagement.removePlayerFreeze(player);

        Lifed.server = null;
    }
}
