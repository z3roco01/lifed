package z3roco01.lifed.util.player;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import z3roco01.lifed.Lifed;

public class ChatUtil {
    /**
     * Sends a message to every player in chat
     * @param message the message contents
     * @param colour the colour of it
     */
    public static void sendChatMessage(String message, Formatting colour) {
        sendChatMessage(Text.of(message).copy().formatted(colour));
    }

    /**
     * Sends a message to the chat for every player
     * @param text the text of the message
     */
    public static void sendChatMessage(Text text) {
        Lifed.SERVER.getPlayerManager().broadcast(text, false);
    }
}
