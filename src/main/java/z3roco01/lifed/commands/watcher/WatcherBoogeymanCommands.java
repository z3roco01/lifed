package z3roco01.lifed.commands.watcher;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import z3roco01.lifed.config.ConfigFiles;
import z3roco01.lifed.features.BoogeymanManager;

public class WatcherBoogeymanCommands extends WatcherCommandRegisterer {
    @Override
    public void registerWatcher(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment, ArgumentBuilder<CommandSourceStack, LiteralArgumentBuilder<CommandSourceStack>> base) {
        dispatcher.register(base.then(Commands.literal("boogeyman")
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
        );
    }
}
