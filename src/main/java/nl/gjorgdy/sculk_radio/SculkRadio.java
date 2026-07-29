package nl.gjorgdy.sculk_radio;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.BlockEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.listeners.OnUseListener;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.RadioNode;
import nl.gjorgdy.sculk_radio.registries.NodeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SculkRadio implements ModInitializer {

    public static final String MOD_ID = "sculk_radio";
    public static Logger LOGGER = LoggerFactory.getLogger("Sculk Radio");

    private static final ConcurrentLinkedQueue<Runnable> serverTasks = new ConcurrentLinkedQueue<>();

    public static void scheduleNextTick(Runnable task) {
        serverTasks.add(task);
    }

    public static Node getNode(ServerLevel serverLevel, BlockPos blockPos) {
        return NodeRegistry.of(serverLevel).getNode(blockPos).orElse(null);
    }

    // start config
    public static float speakerRange = 48f;
    public static int maxNodeRange = 16;
    public static int minAntennaHeight = 16;

    public static int visualsTick = 20;
    public static int redstoneTick = 4;
    public static int connectionTick = 20;

    public static int microphoneRange = 8;
    // end config

    public static boolean microphonesEnabled = false;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            // force load all node registries
            server.getAllLevels().forEach(level -> {
                var registry = NodeRegistry.of(level);
                LOGGER.info("Loaded {} nodes in level {}", registry.size(), level.dimension().identifier());
            });
        });

        ServerTickEvents.START_SERVER_TICK.register(s -> {
            while (!serverTasks.isEmpty()) {
                var task = serverTasks.poll();
                if (task != null) {
                    task.run();
                }
            }
            if (s.getTickCount() % connectionTick == 0) {
                s.getAllLevels().forEach(level -> NodeRegistry.of(level).forSources(SourceNode::connectionTick));
            }
            if (s.getTickCount() % redstoneTick == 0) {
                s.getAllLevels().forEach(level -> NodeRegistry.of(level).forSources(SourceNode::redstoneTick));
            }
            if (s.getTickCount() % visualsTick == 0) {
                s.getAllLevels().forEach(level -> NodeRegistry.of(level).forSources(SourceNode::visualsTick));
            }
        });

        BlockEvents.USE_WITHOUT_ITEM.register(new OnUseListener());

        if (FabricLoader.getInstance().isModLoaded("simple-voice-chat")) {
            microphonesEnabled = true;
        }

        if (FabricLoader.getInstance().isModLoaded("fzzy_config")) {
            FzzyConfig.load();
        } else {
            LOGGER.info("Fzzy Config not found, using default settings.");
        }
    }
}
