package z3roco01.lifed.commands.watcher;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import z3roco01.lifed.commands.CommandRegisterer;

public abstract class WatcherCommandRegisterer implements CommandRegisterer {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        ArgumentBuilder<CommandSourceStack,LiteralArgumentBuilder<CommandSourceStack>> builder = Commands.literal("watcher").requires(Commands.hasPermission(Commands.LEVEL_OWNERS));
        registerWatcher(dispatcher, registryAccess, environment, builder);
    }

    public abstract void registerWatcher(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment, ArgumentBuilder<CommandSourceStack, LiteralArgumentBuilder<CommandSourceStack>> base);
}
