package z3roco01.lifed;

import net.fabricmc.api.ModInitializer;

import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import z3roco01.lifed.event.LifedEvents;
import z3roco01.lifed.lifes.LifeHandler;

public class Lifed implements ModInitializer {
	public static final String MOD_ID = "lifed";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Nullable
    public static MinecraftServer server = null;

    @Override
	public void onInitialize() {
		LOGGER.info("this is our 3rd life");

        LifedEvents.register();
	}
}