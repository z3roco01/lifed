package z3roco01.lifed.util.player;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import z3roco01.lifed.Lifed;

public class TitleUtil {
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

    public static void sendTitleAll(String text, ChatFormatting colour) {
        sendTitleAll(Component.literal(text).copy().withStyle(colour));
    }

    public static void sendTitleAll(Component text) {
        for(ServerPlayer player : Lifed.SERVER.getPlayerList().getPlayers())
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
}
