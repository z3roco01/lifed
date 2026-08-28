package z3roco01.lifed.commands.watcher;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import z3roco01.lifed.features.SessionLock;

import java.util.Collection;
import java.util.UUID;

public class WatcherLockCommands extends WatcherCommandRegisterer {
    @Override
    public void registerWatcher(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment, ArgumentBuilder<CommandSourceStack, LiteralArgumentBuilder<CommandSourceStack>> base) {
        dispatcher.register(base.then(Commands.literal("lock")
                        .then(Commands.literal("status").executes(ctx -> {
                            String feedback = "";
                            if(SessionLock.isLocked())
                                feedback = "§7The session is currently locked !§r";
                            else
                                feedback = "§7The session is currently not locked!§r";

                            String finalFeedback = feedback;
                            ctx.getSource().sendSuccess(() -> Component.literal(finalFeedback), false);

                            return 1;
                        }))
                        .then(Commands.literal("toggle").executes(ctx -> {
                            boolean toggledOn = false;
                            if(SessionLock.isLocked()) {
                                SessionLock.unlock();
                                toggledOn = true;
                            }else
                                SessionLock.lock();
                            SessionLock.lockedMessage = "Session has been locked, new players cannot join, please wait or contact an admin.";

                            String feedback = "";
                            if(!toggledOn)
                                feedback = "§7Session is now locked, only players currently online can rejoin§r";
                            else
                                feedback = "§7Session is now unlocked, anyone can join !§r";

                            String finalFeedback = feedback;
                            ctx.getSource().sendSuccess(() -> Component.literal(finalFeedback), false);

                            return 1;
                        }))
                        .then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.players()).executes(ctx -> {
                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "targets");
                            SessionLock.addPlayers(targets);

                            ctx.getSource().sendSuccess(() -> Component.literal("§7Added " + targets.size() + " players to the allowed players !§r"), false);

                            return 1;
                        })))
                        .then(Commands.literal("adduuid").then(Commands.argument("uuid", UuidArgument.uuid()).executes(ctx -> {
                            UUID uuid = UuidArgument.getUuid(ctx, "uuid");
                            SessionLock.addUUID(uuid);

                            ctx.getSource().sendSuccess(() -> Component.literal("§7Added player with uuid of " + uuid + " !§r"), false);

                            return 1;
                        })))
                ));
    }
}
