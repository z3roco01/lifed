package z3roco01.lifed.features;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import z3roco01.lifed.Lifed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Handles session locking, eg: when boogeys have already been rolled or the session was manually locked
 */
public class SessionLock {
    /**
     * List of players that are allowed to rejoin once the session is locked, stored as uuids
     */
    private static ArrayList<UUID> allowedPlayers = new ArrayList<>();

    /**
     * The variable that actually controls if it is locked
     */
    private static boolean isLocked = false;

    /**
     * The string that is sent to clients when they are disconnected due to lock out
     */
    public static String lockedMessage = "";

    /**
     * Called when a player starts joining the server, will disconnect them when locked
     */
    public static void handlePlayerJoin(ServerPlayNetworkHandler handler, MinecraftServer server) {
        //if((Lifed.config.lockoutPlayers && BoogeymanManager.areBoogeysRolled()) || SessionManagement.sessionLocked)
        //handler.disconnect(Text.of("The boogeymen have already been rolled, try next time or ask the owner"));
        if(SessionLock.isLocked() && !allowedPlayers.contains(handler.player.getUuid()))
            handler.disconnect(Text.of(SessionLock.lockedMessage));
    }

    /**
     * returns if the session is currently locked
     */
    public static boolean isLocked() {
        return isLocked;
    }

    /**
     * Locks the session, must pass a list of allowed players uuids
     * @param allowed the list of the allowed players uuids
     */
    public static void lock(ArrayList<UUID> allowed) {
        allowedPlayers.clear();
        allowedPlayers.addAll(allowed);

        isLocked = true;
    }

    /**
     * Locks the session with a new locked message
     * @param allowed the allowed players
     * @param message the new locked message sent to disallowed players
     */
    public static void lock(ArrayList<UUID> allowed, String message) {
        lock(allowed);
        lockedMessage = message;
    }

    /**
     * Locks the session, using the servers current player list as the allowed players
     */
    public static void lock() {
        ArrayList<UUID> newList = new ArrayList<>();
        for(ServerPlayerEntity player : Lifed.SERVER.getPlayerManager().getPlayerList())
            newList.add(player.getUuid());

        lock(newList);
    }

    /**
     * Locks the session, using the servers current player list as the allowed players
     * @param message new locked out message
     */
    public static void lock(String message) {
        lock();
        lockedMessage = message;
    }

    /**
     * Unlocks the session, allowing new players to join
     */
    public static void unlock() {
        isLocked = false;
    }

    /**
     * Adds another player to the allowed list
     */
    public static void addPlayer(ServerPlayerEntity player) {
        addUUID(player.getUuid());
    }

    /**
     * Adds a collection of players to the allowed list
     */
    public static void addPlayers(Collection<ServerPlayerEntity> players) {
        for(ServerPlayerEntity player : players)
            addUUID(player.getUuid());
    }

    /**
     * Adds a player by their uuid
     */
    public static void addUUID(UUID uuid) {
        allowedPlayers.add(uuid);
    }
}
