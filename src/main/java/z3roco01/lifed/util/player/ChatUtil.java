package z3roco01.lifed.util.player;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import z3roco01.lifed.Lifed;

public class ChatUtil {
    /**
     * Sends a message to every player in chat
     * @param message the message contents
     * @param colour the colour of it
     */
    public static void sendChatMessage(String message, ChatFormatting colour) {
        sendChatMessage(Component.literal(message).copy().withStyle(colour));
    }

    /**
     * Sends a message to the chat for every player
     * @param text the text of the message
     */
    public static void sendChatMessage(Component text) {
        Lifed.SERVER.getPlayerList().broadcastSystemMessage(text, false);
    }
}
