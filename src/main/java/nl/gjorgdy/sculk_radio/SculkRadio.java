package nl.gjorgdy.sculk_radio;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import nl.gjorgdy.sculk_radio.registries.LevelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SculkRadio implements ModInitializer {

    public static final String MOD_ID = "sculk_radio";
    public static Logger LOGGER = LoggerFactory.getLogger("Sculk Radio");

    public static void RunIfServerActive(Runnable runnable) {
        ServerLifecycleEvents.SERVER_STARTED.register(s -> runnable.run());
    }

    // start config
    public static boolean enableExperimentalFrequencies = false;
    public static int innerClusterRange = 16;
    public static int minAntennaHeight = 16;
    // end config

    private static LevelRegistry levelRegistry;
    public static LevelRegistry getLevelRegistry() {
        if (levelRegistry == null) {
            levelRegistry = new LevelRegistry();
        }
        return levelRegistry;
    }

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isModLoaded("fzzy_config")) {
            FzzyConfig.load();
        } else {
            LOGGER.info("Fzzy Config not found, using default settings.");
        }
    }
}
