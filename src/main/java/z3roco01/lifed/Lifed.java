package z3roco01.lifed;

import net.fabricmc.api.ModInitializer;

import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import z3roco01.lifed.config.ConfigFile;
import z3roco01.lifed.config.LifedConfig;
import z3roco01.lifed.event.LifedEvents;

import java.io.IOException;

public class Lifed implements ModInitializer {
	public static final String MOD_ID = "lifed";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Nullable
    public static MinecraftServer SERVER = null;

    public static final LifedConfig config = new LifedConfig();

    @Override
	public void onInitialize() {
		LOGGER.info("this is our 3rd life");

        try {
            ConfigFile.load("./config/lifed.conf", config);
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        LifedEvents.register();
	}
}