package z3roco01.lifed.commands;


import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.features.LifeManager;
import z3roco01.lifed.features.SessionManagement;
import z3roco01.lifed.util.Time;

import java.time.Duration;

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
                    ctx.getSource().sendFeedback(() -> Text.of("§7You have " + LifeManager.getLifeFormatString(executor) +
                            lives + "§7 lives !§r"), false);

                    return 1;
                }));

        dispatcher.register(CommandManager.literal("time")
                .executes(ctx -> {
                    int ticksRemaining = SessionManagement.ticksRemaining();

                    double timePercent = ticksRemaining/(double)Time.MINUTES.ticks(Lifed.config.sessionLength);
                    Lifed.LOGGER.info(String.valueOf(timePercent));

                    String timeColour = "§";
                    // based off colours in limited life
                    if(timePercent >= 0.67)
                        timeColour += "a";
                    else if(timePercent >= 0.34)
                        timeColour += "e";
                    else
                        timeColour += "c";

                    String finalTimeColour = timeColour;
                    ctx.getSource().sendFeedback(() -> Text.of(finalTimeColour + Time.prettyTicks(ticksRemaining)
                            + " §7remaining..."), false);
                    return 1;
                }));
    }
}
