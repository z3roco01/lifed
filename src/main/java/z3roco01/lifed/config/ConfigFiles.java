package z3roco01.lifed.config;

import z3roco01.composed.file.ConfigFile;

import java.io.IOException;

public class ConfigFiles {
    public static final SoulmatesConfig soulmates = new SoulmatesConfig();
    public static final SessionConfig session = new SessionConfig();
    public static final GameplayConfig gameplay = new GameplayConfig();
    public static final BoogeymanConfig boogey = new BoogeymanConfig();

    public static void load() {
        try {
            ConfigFile.load("./config/lifed/soulmates.conf", soulmates);
            ConfigFile.load("./config/lifed/session.conf", session);
            ConfigFile.load("./config/lifed/gameplay.conf", gameplay);
            ConfigFile.load("./config/lifed/boogey.conf", boogey);
        } catch (IOException|IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}
