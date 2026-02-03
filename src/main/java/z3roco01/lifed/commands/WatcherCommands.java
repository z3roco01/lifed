package z3roco01.lifed.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.features.BoogeymanManager;
import z3roco01.lifed.features.LifeManager;
import z3roco01.lifed.features.SessionLock;
import z3roco01.lifed.features.SessionManagement;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class WatcherCommands implements CommandRegisterer {
    @Override
    public void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment
    ) {
        // all the admin commands
        dispatcher.register(CommandManager.literal("watcher")
                .requires(CommandManager.requirePermissionLevel(CommandManager.OWNERS_CHECK))
                                .then(CommandManager.literal("lives")
                                    // command that randomizes every player targeted's lives between 2 and 6 ( inclusive )
                                    .then(CommandManager.literal("roll")
                                        .then(CommandManager.argument("targets", EntityArgumentType.players()).executes(ctx -> {
                                            LifeManager.randomizePlayers(EntityArgumentType.getPlayers(ctx, "targets"));
                                            return 1;
                                        })))

                                    // lets admins set the life count of a player
                                    .then(CommandManager.literal("set")
                                            .then(CommandManager.argument("targets", EntityArgumentType.players())
                                                    .then(CommandManager.argument("lives", IntegerArgumentType.integer()).executes(ctx -> {
                                                        int newLives = IntegerArgumentType.getInteger(ctx, "lives");
                                                        Collection<ServerPlayerEntity> players =
                                                                EntityArgumentType.getPlayers(ctx, "targets");

                                                        for(ServerPlayerEntity player : players)
                                                            LifeManager.setLives(player, newLives);

                                                        return 1;
                                                    }))))

                                    // easy increment and decrement commands
                                    .then(CommandManager.literal("inc")
                                            .then(CommandManager.argument("target", EntityArgumentType.player()).executes(ctx -> {
                                                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "target");

                                                LifeManager.addLife(player);

                                                return 1;
                                            })))
                                    .then(CommandManager.literal("dec")
                                            .then(CommandManager.argument("target", EntityArgumentType.player()).executes(ctx -> {
                                                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "target");

                                                LifeManager.removeLife(player);

                                                return 1;
                                            }))))

                .then(CommandManager.literal("boogeyman")
                        .then(CommandManager.literal("roll").executes(ctx -> {
                                    BoogeymanManager.rollBoogeys(Lifed.config.maxBoogeymen);
                                    return 1;
                                })
                                .then(CommandManager.argument("max", IntegerArgumentType.integer(1)).executes(ctx -> {
                                    BoogeymanManager.rollBoogeys(IntegerArgumentType.getInteger(ctx, "max"));
                                    return 1;
                                })))
                        .then(CommandManager.literal("cure")
                                .then(CommandManager.argument("target", EntityArgumentType.player()).executes(ctx -> {
                                    BoogeymanManager.cure(EntityArgumentType.getPlayer(ctx, "target"));
                                    return 1;
                                })))
                        .then(CommandManager.literal("fail")
                                .then(CommandManager.argument("target", EntityArgumentType.player()).executes(ctx -> {
                                    BoogeymanManager.fail(EntityArgumentType.getPlayer(ctx, "target"));
                                    return 1;
                                })))
                        .then(CommandManager.literal("failall").executes(ctx -> {
                            BoogeymanManager.failAll();
                            return 1;
                        }))
                        .then(CommandManager.literal("reset").executes(ctx -> {
                            BoogeymanManager.clearBoogeymen();
                            return 1;
                        })))
                .then(CommandManager.literal("session")
                        .then(CommandManager.literal("start").executes(ctx -> {
                            SessionManagement.unpause();
                            return 1;
                        }))

                        .then(CommandManager.literal("break").executes(ctx -> {
                            SessionManagement.goOnBreak();
                            return 1;
                        }))

                        .then(CommandManager.literal("cancelbreak").executes(ctx -> {
                            SessionManagement.goOffBreak();
                            return 1;
                        }))

                        .then(CommandManager.literal("stop").executes(ctx -> {
                            SessionManagement.pause();
                            return 1;
                        }))
                )
                .then(CommandManager.literal("lock")
                        .then(CommandManager.literal("status").executes(ctx -> {
                            String feedback = "";
                            if(SessionLock.isLocked())
                                feedback = "§7The session is currently locked !§r";
                            else
                                feedback = "§7The session is currently not locked!§r";

                            String finalFeedback = feedback;
                            ctx.getSource().sendFeedback(() -> Text.of(finalFeedback), false);

                            return 1;
                        }))
                        .then(CommandManager.literal("toggle").executes(ctx -> {
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
                            ctx.getSource().sendFeedback(() -> Text.of(finalFeedback), false);

                            return 1;
                        }))
                        .then(CommandManager.literal("add").then(CommandManager.argument("targets", EntityArgumentType.players()).executes(ctx -> {
                            Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
                            SessionLock.addPlayers(targets);

                            ctx.getSource().sendFeedback(() -> Text.of("§7Added " + targets.size() + " players to the allowed players !§r"), false);

                            return 1;
                        })))
                        .then(CommandManager.literal("adduuid").then(CommandManager.argument("uuid", UuidArgumentType.uuid()).executes(ctx -> {
                            UUID uuid = UuidArgumentType.getUuid(ctx, "uuid");
                            SessionLock.addUUID(uuid);

                            ctx.getSource().sendFeedback(() -> Text.of("§7Added player with uuid of " + uuid + " !§r"), false);

                            return 1;
                        })))
                )
        );

        // only register the debug commands when theyre enabled, since they can be kinda cheaty
        if(Lifed.config.watcherDebug) {
            dispatcher.register(CommandManager.literal("watcher")
                    .then(CommandManager.literal("debug")
                            .then(CommandManager.literal("boogeychance")
                                    .then(CommandManager.argument("chance", FloatArgumentType.floatArg(0f, 1f)).executes(ctx -> {
                                        Lifed.config.sequentialBoogeyChange = FloatArgumentType.getFloat(ctx, "chance");
                                        ctx.getSource().sendFeedback(() -> Text.of("new chance: " + Lifed.config.sequentialBoogeyChange), false);

                                        return 1;
                                    })))
                            .then(CommandManager.literal("instantboogey").executes(ctx -> {
                                BoogeymanManager.clearBoogeymen();

                                BoogeymanManager.selectBoogeys(Lifed.config.maxBoogeymen);

                                List<ServerPlayerEntity> players = Lifed.SERVER.getPlayerManager().getPlayerList();
                                BoogeymanManager.showBoogeyStatus(players);
                                return 1;
                            }))
                            .then(CommandManager.literal("printboogeys").executes(ctx -> {
                                for(ServerPlayerEntity boogey : BoogeymanManager.getBoogeymen())
                                    Lifed.LOGGER.info(boogey.getStringifiedName());

                                return 1;
                            }))
                    )
            );
        }
    }
}
