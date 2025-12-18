package z3roco01.lifed.util;

import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TitleUtil {
    /**
     * Sends a title to the player, with a string and colour
     * @param target the player to send it to
     * @param text the text contents of the title
     * @param colour the colour of the title
     */
    public static void sendTitle(ServerPlayerEntity target, String text, Formatting colour) {
        sendTitle(target, Text.of(text).copy().formatted(colour));
    }

    /**
     * Sends a TitleS2C packet to the specified player
     * @param target player to send the title to
     * @param text the text of the title
     */
    public static void sendTitle(ServerPlayerEntity target, Text text) {
        target.networkHandler.sendPacket(new TitleS2CPacket(text));
    }
}
