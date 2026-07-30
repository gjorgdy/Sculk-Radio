package nl.gjorgdy.sculk_radio;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.BlockEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.events.ConfigCallback;
import nl.gjorgdy.sculk_radio.listeners.OnUseListener;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;
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
    public static int microphoneRange = 8;

    public static int visualsTick = 20;
    public static int redstoneTick = 4;
    public static int connectionTick = 20;

    public static boolean forceSyncSpeakers = false;
    public static boolean speakerCategory = false;
    public static boolean microphonesEnabledConfig = true;
    public static boolean redstoneEnabled = true;
    public static boolean antennasEnabled = true;
    // end config

    public static boolean voiceChatInstalled = false;
    public static boolean microphonesEnabled() {
        return voiceChatInstalled && microphonesEnabledConfig;
    }

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
            if (SculkRadio.redstoneEnabled && s.getTickCount() % redstoneTick == 0) {
                s.getAllLevels().forEach(level -> NodeRegistry.of(level).forSources(SourceNode::redstoneTick));
            }
            if (s.getTickCount() % visualsTick == 0) {
                s.getAllLevels().forEach(level -> NodeRegistry.of(level).forSources(SourceNode::visualsTick));
            }
        });

        var useListener = new OnUseListener();
        BlockEvents.USE_WITHOUT_ITEM.register(useListener);
        BlockEvents.USE_ITEM_ON.register(useListener);

        if (FabricLoader.getInstance().isModLoaded("voicechat")) {
            voiceChatInstalled = true;
        }
        if (FabricLoader.getInstance().isModLoaded("fzzy_config")) {
            FzzyConfig.firstLoad();
        } else {
            LOGGER.info("Fzzy Config not found, using default settings.");
            ConfigCallback.RELOAD_CONFIG.invoker().onReload();
        }
    }
}
