package z3roco01.lifed.features;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.config.ConfigFiles;
import z3roco01.lifed.util.TaskScheduling;
import z3roco01.lifed.util.Time;
import z3roco01.lifed.util.player.ChatUtil;
import z3roco01.lifed.util.player.TitleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles everything to do with soulmates ( feature similar to Double Life's gimmick )
 */
public class SoulmateManager {
    private static final SoulmateList soulmates = new SoulmateList();

    public static @Nullable UUID getSoulmate(UUID uuid) {
        SoulmatePair pair = soulmates.getPairContaining(uuid);
        if(pair == null) return null;

        if(pair.player1.equals(uuid)) return pair.player2;
        return pair.player1;
    }

    public static void clearSoulmates() {
        for(SoulmatePair pair : soulmates.pairs) {
            ((SoulmateHaver)pair.getPlayer1()).setSoulmate(null);
            ((SoulmateHaver)pair.getPlayer2()).setSoulmate(null);
        }
        soulmates.reset();
    }

    /**
     * Roll all players ( except one uneven player if exists ) into pairs of soulmates, removing all old pairings
     */
    public static void rollSoulmates() {
        clearSoulmates();
        // copy of players
        ArrayList<ServerPlayer> players = new ArrayList<>(Lifed.server.getPlayerList().getPlayers());
        // hacky little source of random
        RandomSource random = players.get(0).getRandom();

        int playersCount = players.size();
        if(playersCount < 2)
            return;

        // not even player count, one unmatched player
        if(players.size() % 2 == 1) {
            playersCount -= 1;
            ChatUtil.sendChatMessage("WARNING !!!! Uneven player count, one player will not have a soulmate", ChatFormatting.RED);
        }

        int pairCount = playersCount/2;

        for(int i = 0; i < pairCount; ++i) {
            int firstIdx = getRandom(random, players);
            ServerPlayer firstPlayer = players.get(firstIdx);
            int idx = getRandom(random, players);
            ServerPlayer secondPlayer = players.get(idx);

            // make sure these players are not the same
            while(firstIdx == idx) {
                idx = getRandom(random, players);
                secondPlayer = players.get(idx);
            }

            // create pair
            soulmates.addPair(firstPlayer.getUUID(), secondPlayer.getUUID());

            // remove from choosable players
            players.remove(firstPlayer);
            players.remove(secondPlayer);
        }

        soulmateReveal();

        for(SoulmatePair pair : soulmates.pairs) {
            initPair(pair);
        }
    }

    /**
     * Countdown,then if supposed to, reveal soulmates, or show ???
     */
    private static void soulmateReveal() {
        TitleUtil.threeSecondCountdown(() -> {
            TitleUtil.sendTitleAll(Component.translatable("lifed.soulmate_wait").getString(), ChatFormatting.GREEN);
            TaskScheduling.scheduleTask(Time.SECONDS.ticks(5), () -> {
                // send titles to each pair now, and maybe reveal
                if(ConfigFiles.soulmates.revealSoulmates) {
                    for(SoulmatePair pair : soulmates.pairs) {
                        ServerPlayer player1 = pair.getPlayer1();
                        ServerPlayer player2 = pair.getPlayer2();
                        TitleUtil.sendTitle(player1, player2.getPlainTextName(), ChatFormatting.GREEN);
                        TitleUtil.sendTitle(player2, player1.getPlainTextName(), ChatFormatting.GREEN);
                    }
                }else {
                    TitleUtil.sendTitleAll(Component.translatable("lifed.soulmate_unknown").getString(), ChatFormatting.GREEN);
                }
            });
        });
    }

    private static int getRandom(RandomSource random, ArrayList<?> list) {
        return random.nextInt(0, list.size());
    }

    /**
     * Initialise a pair to the starting life count, and fills their health, hunger and saturation
     */
    private static void initPair(SoulmatePair pair) {
        for(ServerPlayer player : pair.getPlayers()) {
            LifeManager.setLives(player, ConfigFiles.soulmates.soulmatesLives);

            player.setHealth(player.getMaxHealth());

            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(5);
        }
    }

    /**
     * Syncs a pairs stats to the lowest of the two
     * @param player one of the two players apair of the pair
     */
    public static void syncPair(ServerPlayer player) {
        Lifed.LOGGER.info("sync");
        SoulmatePair pair = soulmates.getPairContaining(player.getUUID());
        if(pair == null)
            return;

        ServerPlayer player1 = pair.getPlayer1();
        ServerPlayer player2 = pair.getPlayer2();

        // bruh
        if(player1 == null && player2 == null)
            return;

        // only one of them can be null, set it to the calling player
        if(player1 == null)
            player1 = player;
        else if(player2 == null)
            player2 = player;

        int lowestLives = Math.min(LifeManager.getLives(player1), LifeManager.getLives(player2));
        float lowestHealth = Math.min(player1.getHealth(), player2.getHealth());
        int lowestHunger = Math.min(player1.getFoodData().getFoodLevel(), player2.getFoodData().getFoodLevel());
        float lowestSaturation = Math.min(player1.getFoodData().getSaturationLevel(), player2.getFoodData().getSaturationLevel());
        Lifed.LOGGER.info(lowestHealth + " " + lowestHunger + " " + lowestSaturation);

        LifeManager.setLives(player1, lowestLives);
        LifeManager.setLives(player2, lowestLives);

        player1.setHealth(lowestHealth);
        player2.setHealth(lowestHealth);

        player1.getFoodData().setFoodLevel(lowestHunger);
        player2.getFoodData().setFoodLevel(lowestHunger);

        player1.getFoodData().setSaturation(lowestSaturation);
        player2.getFoodData().setSaturation(lowestSaturation);
    }

    static class SoulmateList {
        private final ArrayList<SoulmatePair> pairs = new ArrayList<>();

        public void addPair(UUID player1, UUID player2) {
            addPair(new SoulmatePair(player1, player2));
        }

        public void addPair(SoulmatePair pair) {
            pairs.add(pair);
            syncToPlayers(pair);
        }

        /**
         * Syncs the pairing to the server players, so stuff can be shared
         */
        private void syncToPlayers(SoulmatePair pair) {
            ((SoulmateHaver)pair.getPlayer1()).setSoulmate(pair.player2);
            ((SoulmateHaver)pair.getPlayer2()).setSoulmate(pair.player1);
        }

        /**
         * Gets pair containing this player
         */
        public @Nullable SoulmatePair getPairContaining(ServerPlayer player) {
            for(SoulmatePair pair : pairs) {
                if(pair.getUUIDS().contains(player))
                    return pair;
            }
            return null;
        }

        public @Nullable SoulmatePair getPairContaining(UUID uuid) {
            for(SoulmatePair pair : pairs) {
                if(pair.player1.equals(uuid) || pair.player2.equals(uuid))
                    return pair;
            }
            return null;
        }

        public void reset() {
            pairs.clear();
        }

        /**
         * Returns true if a player has been put in a pair already
         */
        public boolean isPlayerPaired(ServerPlayer player) {
            for(SoulmatePair pair : pairs) {
                if(pair.getUUIDS().contains(player))
                    return true;
            }

            return false;
        }
    }

    /**
     * Class to easily hold one pair of soulmates
     */
    static class SoulmatePair {
        public final UUID player1;
        public final UUID player2;

        public SoulmatePair(UUID player1, UUID player2) {
            this.player1 = player1;
            this.player2 = player2;
        }

        public List<UUID> getUUIDS() {
            return List.of(player1, player2);
        }

        public ServerPlayer getPlayer1() {
            return Lifed.server.getPlayerList().getPlayer(player1);
        }

        public ServerPlayer getPlayer2() {
            return Lifed.server.getPlayerList().getPlayer(player2);
        }

        public List<ServerPlayer> getPlayers() {
            return List.of(getPlayer1(), getPlayer2());
        }
    }

    /**
     * Used in ServerPlayerMixin, so that the player object is aware of its soulmate
     */
    public interface SoulmateHaver {
        void setSoulmate(UUID soulmate);
        @Nullable
        UUID getSoulmate();
        @Nullable
        ServerPlayer getSoulmatePlayer();

        void incomingDamage(float damage);
        float getIncomingDamage();
        void clearIncoming();
    }
}
