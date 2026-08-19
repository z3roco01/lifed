package z3roco01.lifed.features;

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
        LifeManager.randomizePlayers(Lifed.server.getPlayerList().getPlayers());
    }

    private static void rollBoogeys() {
        BoogeymanManager.rollBoogeys(ConfigFiles.boogey.maxBoogeymen);
    }

    private static void rollSoulmates() {
        SoulmateManager.rollSoulmates();
    }
}
