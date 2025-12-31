package z3roco01.lifed.commands;


import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import z3roco01.lifed.features.LifeManager;

/**
 * Commands that normal players can interact with
 */
public class PlayerCommands implements CommandRegisterer {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        // lets a player gift one of their lives
        dispatcher.register(CommandManager.literal("givelife")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                        .executes(ctx -> {
                            ServerPlayerEntity gifter = ctx.getSource().getPlayer();
                            ServerPlayerEntity recipient = EntityArgumentType.getPlayer(ctx, "target");

                            // if the gift was unsuccessful, give feedback
                            if(!LifeManager.giftLife(gifter, recipient)) {
                                ctx.getSource().sendFeedback(() -> Text.of("Could not gift a life to " + recipient.getStringifiedName())
                                        .copy().formatted(Formatting.RED), false);
                                return 0;
                            }

                            return 1;
                        })));

        dispatcher.register(CommandManager.literal("lives")
                .executes(ctx -> {
                    ServerPlayerEntity executor = ctx.getSource().getPlayer();

                    int lives = LifeManager.getLives(executor);
                    ctx.getSource().sendFeedback(() -> Text.of("You have " + lives + " lives !"), false);

                    return 1;
                }));
    }
}
