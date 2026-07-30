package z3roco01.lifed.commands;


import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.features.BoogeymanManager;
import z3roco01.lifed.features.LifeManager;
import z3roco01.lifed.features.SessionManagement;
import z3roco01.lifed.util.Time;

/**
 * Commands that normal players can interact with
 */
public class PlayerCommands implements CommandRegisterer {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        // lets a player gift one of their lives
        dispatcher.register(Commands.literal("givelife")
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(ctx -> {
                            ServerPlayer gifter = ctx.getSource().getPlayer();
                            ServerPlayer recipient = EntityArgument.getPlayer(ctx, "target");

                            // if the gift was unsuccessful, give feedback
                            if(!LifeManager.giftLife(gifter, recipient)) {
                                ctx.getSource().sendSuccess(() -> Component.literal("Could not gift a life to " + recipient.getPlainTextName())
                                        .copy().withStyle(ChatFormatting.RED), false);
                                return 0;
                            }

                            return 1;
                        })));

        dispatcher.register(Commands.literal("lives")
                .executes(ctx -> {
                    ServerPlayer executor = ctx.getSource().getPlayer();

                    int lives = LifeManager.getLives(executor);
                    ctx.getSource().sendSuccess(() -> Component.literal("§7You have " + LifeManager.getLifeFormatString(executor) +
                            lives + "§7 lives !§r"), false);

                    return 1;
                }));

        dispatcher.register(Commands.literal("remaining")
                .executes(ctx -> {
                    if(SessionManagement.onBreak()) {
                        ctx.getSource().sendSuccess(() -> Component.literal("§7" + Time.prettyTicks(SessionManagement.remainingBreakTicks()) +
                                " remaining in the break"), false);
                    }else {
                        int ticksRemaining = SessionManagement.ticksRemaining();

                        double timePercent = ticksRemaining/(double)Time.MINUTES.ticks(Lifed.config.sessionLength);

                        String timeColour = "§";
                        // based off colours in limited life
                        if(timePercent >= 2f/3f)
                            timeColour += "a";
                        else if(timePercent >= 1f/3f)
                            timeColour += "e";
                        else
                            timeColour += "c";

                        String finalTimeColour = timeColour;
                        ctx.getSource().sendSuccess(() -> Component.literal(finalTimeColour + Time.prettyTicks(ticksRemaining)
                                + " §7remaining..."), false);
                    }
                    return 1;
                }));

        dispatcher.register(Commands.literal("boogeys").executes(ctx -> {
            if(BoogeymanManager.getBoogeymen().isEmpty())
                ctx.getSource().sendSuccess(() -> Component.literal("There are no boogeymen remaining").copy().withStyle(ChatFormatting.GRAY), false);
            else
                ctx.getSource().sendSuccess(() -> Component.literal("There is at least one boogeyman remaining").copy().withStyle(ChatFormatting.GRAY), false);

            return 1;
        }));

        dispatcher.register(Commands.literal("amiboogey").executes(ctx -> {
            ServerPlayer player  = ctx.getSource().getPlayer();
            if(BoogeymanManager.isPlayerBoogey(player))
                ctx.getSource().sendSuccess(() -> Component.literal("You ARE a boogeyman").copy().withStyle(ChatFormatting.RED), false);
            else
                ctx.getSource().sendSuccess(() -> Component.literal("You ARE NOT a boogeyman").copy().withStyle(ChatFormatting.GREEN), false);

            return 1;
        }));
    }
}
