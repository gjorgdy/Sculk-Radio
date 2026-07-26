package nl.gjorgdy.sculk_radio;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.BlockEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.listeners.OnUseListener;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.registries.NodeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SculkRadio implements ModInitializer {

    public static final String MOD_ID = "sculk_radio";
    public static Logger LOGGER = LoggerFactory.getLogger("Sculk Radio");

    public static Node getNode(ServerLevel serverLevel, BlockPos blockPos) {
        return NodeRegistry.of(serverLevel).getNode(blockPos).orElse(null);
    }

    // start config
    public static boolean enableExperimentalFrequencies = false;
    public static int innerClusterRange = 16;
    public static int minAntennaHeight = 16;
    // end config

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            // force load all node registries
            server.getAllLevels().forEach(level -> {
                var registry = NodeRegistry.of(level);
                LOGGER.info("Loaded {} nodes in level {}", registry.size(), level.dimension().identifier());
            });
        });

        BlockEvents.USE_WITHOUT_ITEM.register(new OnUseListener());

        if (FabricLoader.getInstance().isModLoaded("fzzy_config")) {
            FzzyConfig.load();
        } else {
            LOGGER.info("Fzzy Config not found, using default settings.");
        }
    }
}
