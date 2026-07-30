package z3roco01.lifed.util.player;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.util.TaskScheduling;
import z3roco01.lifed.util.Time;

public class TitleUtil {
    public static void sendTitle(ServerPlayer target, String text) {
        sendTitle(target, text, ChatFormatting.RESET);
    }

    /**
     * Sends a title to the player, with a string and colour
     * @param target the player to send it to
     * @param text the text contents of the title
     * @param colour the colour of the title
     */
    public static void sendTitle(ServerPlayer target, String text, ChatFormatting colour) {
        sendTitle(target, Component.literal(text).copy().withStyle(colour));
    }

    /**
     * Sends a TitleS2C packet to the specified player
     * @param target player to send the title to
     * @param text the text of the title
     */
    public static void sendTitle(ServerPlayer target, Component text) {
        target.connection.send(new ClientboundSetTitleTextPacket(text));
    }

    public static void sendTitleAll(String text) {
        sendTitleAll(text, ChatFormatting.RESET);
    }

    public static void sendTitleAll(String text, ChatFormatting colour) {
        sendTitleAll(Component.literal(text).copy().withStyle(colour));
    }

    public static void sendTitleAll(Component text) {
        for(ServerPlayer player : Lifed.server.getPlayerList().getPlayers())
            sendTitle(player, text);
    }

    /**
     * Sends a title and chat message to every player
     * @param text text to send
     * @param colour the formatting of the text when sent
     */
    public static void sendTitleAndChat(String text, ChatFormatting colour) {
        sendTitleAndChat(Component.literal(text).copy().withStyle(colour));
    }

    /**
     * Sends a title and chat message to every player
     * @param text text to send
     */
    public static void sendTitleAndChat(Component text) {
        sendTitleAll(text);
        ChatUtil.sendChatMessage(text);
    }

    /**
     * Show a three second countdown
     * @param afterCountdown code to run after the countdown has finished
     */
    public static void threeSecondCountdown(@Nullable Runnable afterCountdown) {
        sendTitleAll("3", ChatFormatting.GREEN);
        TaskScheduling.scheduleTask(Time.SECONDS.ticks(1), () -> {
            sendTitleAll("2", ChatFormatting.YELLOW);
            TaskScheduling.scheduleTask(Time.SECONDS.ticks(1), () -> {
                sendTitleAll("1", ChatFormatting.RED);
                if(afterCountdown != null)
                    TaskScheduling.scheduleTask(Time.SECONDS.ticks(1), afterCountdown);
            });
        });
    }
}
