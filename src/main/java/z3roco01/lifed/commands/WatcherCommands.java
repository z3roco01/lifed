package z3roco01.lifed.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.config.ConfigFiles;
import z3roco01.lifed.features.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class WatcherCommands implements CommandRegisterer {
    @Override
    public void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext registryAccess,
            Commands.CommandSelection environment
    ) {
        // all the admin commands
        dispatcher.register(Commands.literal("watcher")
                .requires(Commands.hasPermission(Commands.LEVEL_OWNERS))
                .then(Commands.literal("lives")
                    // command that randomizes every player targeted's lives between 2 and 6 ( inclusive )
                    .then(Commands.literal("roll")
                        .then(Commands.argument("targets", EntityArgument.players()).executes(ctx -> {
                            LifeManager.randomizePlayers(EntityArgument.getPlayers(ctx, "targets"));
                            return 1;
                        })))

                    // lets admins set the life count of a player
                    .then(Commands.literal("set")
                            .then(Commands.argument("targets", EntityArgument.players())
                                    .then(Commands.argument("lives", IntegerArgumentType.integer()).executes(ctx -> {
                                        int newLives = IntegerArgumentType.getInteger(ctx, "lives");
                                        Collection<ServerPlayer> players =
                                                EntityArgument.getPlayers(ctx, "targets");

                                        for(ServerPlayer player : players)
                                            LifeManager.setLives(player, newLives);

                                        return 1;
                                    }))))

                    // easy increment and decrement commands
                    .then(Commands.literal("inc")
                            .then(Commands.argument("target", EntityArgument.player()).executes(ctx -> {
                                ServerPlayer player = EntityArgument.getPlayer(ctx, "target");

                                LifeManager.addLife(player);

                                return 1;
                            })))
                    .then(Commands.literal("dec")
                            .then(Commands.argument("target", EntityArgument.player()).executes(ctx -> {
                                ServerPlayer player = EntityArgument.getPlayer(ctx, "target");

                                LifeManager.removeLife(player);

                                return 1;
                            }))))
                .then(Commands.literal("boogeyman")
                        .then(Commands.literal("roll").executes(ctx -> {
                                    BoogeymanManager.rollBoogeys(ConfigFiles.boogey.maxBoogeymen);
                                    return 1;
                                })
                                .then(Commands.argument("max", IntegerArgumentType.integer(1)).executes(ctx -> {
                                    BoogeymanManager.rollBoogeys(IntegerArgumentType.getInteger(ctx, "max"));
                                    return 1;
                                })))
                        .then(Commands.literal("cure")
                                .then(Commands.argument("target", EntityArgument.player()).executes(ctx -> {
                                    BoogeymanManager.cure(EntityArgument.getPlayer(ctx, "target"));
                                    return 1;
                                })))
                        .then(Commands.literal("fail")
                                .then(Commands.argument("target", EntityArgument.player()).executes(ctx -> {
                                    BoogeymanManager.fail(EntityArgument.getPlayer(ctx, "target"));
                                    return 1;
                                })))
                        .then(Commands.literal("failall").executes(ctx -> {
                            BoogeymanManager.failAll();
                            return 1;
                        }))
                        .then(Commands.literal("reset").executes(ctx -> {
                            BoogeymanManager.clearBoogeymen();
                            return 1;
                        })))
                .then(Commands.literal("session")
                        .then(Commands.literal("start").executes(ctx -> {
                            SessionManager.start();
                            return 1;
                        }))

                        .then(Commands.literal("start").then(Commands.argument("minutes", IntegerArgumentType.integer(1)).executes(ctx -> {
                            SessionManager.start(IntegerArgumentType.getInteger(ctx, "minutes"));
                            return 1;
                        })))

                        .then(Commands.literal("unpause").executes(ctx -> {
                            SessionManager.unpause();
                            return 1;
                        }))

                        .then(Commands.literal("break").executes(ctx -> {
                            SessionManager.goOnBreak();
                            return 1;
                        }))

                        .then(Commands.literal("cancelbreak").executes(ctx -> {
                            SessionManager.goOffBreak();
                            return 1;
                        }))

                        .then(Commands.literal("pause").executes(ctx -> {
                            SessionManager.pause();
                            return 1;
                        }))
                        .then(Commands.literal("first").executes(ctx -> {
                            AutoSetup.firstSession();
                            return 1;
                        }))
                )
                .then(Commands.literal("lock")
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
                )
                .then(Commands.literal("soulmates")
                        .then(Commands.literal("roll").executes(ctx -> {
                            SoulmateManager.rollSoulmates();

                            return 1;
                        }))
                        .then(Commands.literal("clear").executes(ctx -> {
                            SoulmateManager.clearSoulmates();

                            return 1;
                        }))
                )
        );

        // only register the debug commands when theyre enabled, since they can be kinda cheaty
        if(ConfigFiles.gameplay.watcherDebug) {
            dispatcher.register(Commands.literal("watcher")
                    .then(Commands.literal("debug")
                            .then(Commands.literal("boogeychance")
                                    .then(Commands.argument("chance", FloatArgumentType.floatArg(0f, 1f)).executes(ctx -> {
                                        ConfigFiles.boogey.sequentialBoogeyChange = FloatArgumentType.getFloat(ctx, "chance");
                                        ctx.getSource().sendSuccess(() -> Component.literal("new chance: " + ConfigFiles.boogey.sequentialBoogeyChange), false);

                                        return 1;
                                    })))
                            .then(Commands.literal("instantboogey").executes(ctx -> {
                                BoogeymanManager.clearBoogeymen();

                                BoogeymanManager.selectBoogeys(ConfigFiles.boogey.maxBoogeymen);

                                List<ServerPlayer> players = Lifed.server.getPlayerList().getPlayers();
                                BoogeymanManager.showBoogeyStatus(players);
                                return 1;
                            }))
                            .then(Commands.literal("printboogeys").executes(ctx -> {
                                for(UUID boogey : BoogeymanManager.getBoogeymen())
                                    Lifed.LOGGER.info(boogey.toString());

                                return 1;
                            }))
                    )
            );
        }
    }
}
