package z3roco01.lifed.commands.watcher;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.config.ConfigFiles;
import z3roco01.lifed.features.BoogeymanManager;

import java.util.List;
import java.util.UUID;

public class WatcherDebugCommands extends WatcherCommandRegisterer {
    @Override
    public void registerWatcher(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment, ArgumentBuilder<CommandSourceStack, LiteralArgumentBuilder<CommandSourceStack>> base) {
        // only register the debug commands when theyre enabled, since they can be kinda cheaty
        if(ConfigFiles.gameplay.watcherDebug) {
            dispatcher.register(base.then(Commands.literal("debug")
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
