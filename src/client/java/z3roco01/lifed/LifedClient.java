package z3roco01.lifed;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifedClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Lifed.MOD_ID + "-client");

	@Override
	public void onInitializeClient() {
        LOGGER.info("this is your 3rd life");
	}
}