package z3roco01.lifed.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.features.BoogeymanManager;
import z3roco01.lifed.features.LifeManager;

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
                                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                            .executes(ctx -> {
                                                LifeManager.randomizePlayers(EntityArgumentType.getPlayers(ctx, "targets"));
                                                return 1;
                                            })
                                        ))

                                    // lets admins set the life count of a player
                                    .then(CommandManager.literal("set")
                                            .then(CommandManager.argument("target", EntityArgumentType.player())
                                                    .then(CommandManager.argument("lives", IntegerArgumentType.integer())
                                                            .executes(ctx -> {
                                                                int newLives = IntegerArgumentType.getInteger(ctx, "lives");
                                                                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "target");

                                                                LifeManager.setLives(player, newLives);

                                                                return 1;
                                                            }))))

                                    // easy increment and decrement commands
                                    .then(CommandManager.literal("inc")
                                            .then(CommandManager.argument("target", EntityArgumentType.player())
                                                    .executes(ctx -> {
                                                        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "target");

                                                        LifeManager.addLife(player);

                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("dec")
                                            .then(CommandManager.argument("target", EntityArgumentType.player())
                                                    .executes(ctx -> {
                                                        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "target");

                                                        LifeManager.removeLife(player);

                                                        return 1;
                                                    }))))

                .then(CommandManager.literal("boogeyman")
                        .then(CommandManager.literal("roll")
                                .executes(ctx -> {
                                    BoogeymanManager.rollBoogeys(Lifed.config.maxBoogeymen);
                                    return 1;
                                })
                                .then(CommandManager.argument("max", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            BoogeymanManager.rollBoogeys(IntegerArgumentType.getInteger(ctx, "max"));
                                            return 1;
                                        })
                                )
                        )
                        .then(CommandManager.literal("cure")
                                .then(CommandManager.argument("target", EntityArgumentType.player())
                                        .executes(ctx -> {
                                            BoogeymanManager.cure(EntityArgumentType.getPlayer(ctx, "target"));
                                            return 1;
                                        })
                                ))
                        .then(CommandManager.literal("fail")
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .executes(ctx -> {
                                            for(ServerPlayerEntity player : EntityArgumentType.getPlayers(ctx, "targets"))
                                                BoogeymanManager.fail(player);
                                            return 1;
                                        })
                                ))
                        .then(CommandManager.literal("reset")
                                .executes(ctx -> {
                                    BoogeymanManager.clearBoogeymen();
                                    return 1;
                                }))));
    }
}
