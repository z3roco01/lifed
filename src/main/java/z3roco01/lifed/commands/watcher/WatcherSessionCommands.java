package z3roco01.lifed.commands.watcher;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import z3roco01.lifed.features.AutoSetup;
import z3roco01.lifed.features.SessionManager;

public class WatcherSessionCommands extends WatcherCommandRegisterer{
    @Override
    public void registerWatcher(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment, ArgumentBuilder<CommandSourceStack, LiteralArgumentBuilder<CommandSourceStack>> base) {
        dispatcher.register(base.then(Commands.literal("session")
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
                .then(Commands.literal("add").then(Commands.argument("minutes", IntegerArgumentType.integer(1)).executes(ctx -> {
                    SessionManager.addMinutes(IntegerArgumentType.getInteger(ctx, "minutes"));
                    return 1;
                })))
                .then(Commands.literal("sub").then(Commands.argument("minutes", IntegerArgumentType.integer(1)).executes(ctx -> {
                    SessionManager.subMinutes(IntegerArgumentType.getInteger(ctx, "minutes"));
                    return 1;
                })))
        ));
    }
}
