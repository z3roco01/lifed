package z3roco01.lifed.commands.watcher;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import z3roco01.lifed.features.SoulmateManager;

public class WatcherSoulmatesCommands extends WatcherCommandRegisterer {
    @Override
    public void registerWatcher(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment, ArgumentBuilder<CommandSourceStack, LiteralArgumentBuilder<CommandSourceStack>> base) {
        dispatcher.register(base.then(Commands.literal("soulmates")
                        .then(Commands.literal("roll").executes(ctx -> {
                            SoulmateManager.rollSoulmates();

                            return 1;
                        }))
                        .then(Commands.literal("clear").executes(ctx -> {
                            SoulmateManager.clearSoulmates();

                            return 1;
                        }))
                ));
    }
}
