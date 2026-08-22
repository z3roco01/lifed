package z3roco01.lifed.commands.watcher;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import z3roco01.lifed.features.LifeManager;

import java.util.Collection;

public class WatcherLivesCommands extends WatcherCommandRegisterer {
    @Override
    public void registerWatcher(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment, ArgumentBuilder<CommandSourceStack, LiteralArgumentBuilder<CommandSourceStack>> base) {
        dispatcher.register(base.then(Commands.literal("lives")
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
        );
    }
}
