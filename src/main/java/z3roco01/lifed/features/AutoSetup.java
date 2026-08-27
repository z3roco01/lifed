package z3roco01.lifed.features;

import net.minecraft.server.level.ServerPlayer;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.config.ConfigFiles;

public class AutoSetup {
    /**
     * Registers session callbacks for the first session setup
     */
    public static void firstSession() {
        if(ConfigFiles.autosetup.rollSoulmates)
            SessionManager.registerStartEvent(AutoSetup::rollSoulmates);

        if(ConfigFiles.autosetup.rollLives)
            SessionManager.registerStartEvent(AutoSetup::rollLives);

        if(ConfigFiles.autosetup.startingLives >= 0)
            SessionManager.registerStartEvent(AutoSetup::setLives);
    }

    /**
     * Registers event that trigger no matter what number session this is
     */
    public static void registerNormal() {
        if(ConfigFiles.autosetup.rollBoogeys)
            SessionManager.registerStartEvent(AutoSetup::rollBoogeys);
    }

    /**
     * Roll all players lives
     */
    private static void rollLives() {
        LifeManager.randomizePlayers(Lifed.server.getPlayerList().getPlayers(), ConfigFiles.autosetup.rollMin, ConfigFiles.autosetup.rollMax);
    }

    private static void rollBoogeys() {
        BoogeymanManager.rollBoogeys(ConfigFiles.boogey.maxBoogeymen);
    }

    private static void rollSoulmates() {
        SoulmateManager.rollSoulmates();
    }

    // set everyones lives constant
    private static void setLives() {
        for(ServerPlayer player : Lifed.server.getPlayerList().getPlayers())
            LifeManager.setLives(player, ConfigFiles.autosetup.startingLives);
    }
}
