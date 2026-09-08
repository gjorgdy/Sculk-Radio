package nl.gjorgdy.sculk_radio;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;
import nl.gjorgdy.sculk_radio.registries.NodeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

abstract public class SculkRadio {

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
    public static boolean teleportEnabled = false;
    public static boolean tuningEnabled = true;
    // end config

    public static boolean voiceChatInstalled = false;
    public static boolean microphonesEnabled() {
        return voiceChatInstalled && microphonesEnabledConfig;
    }

    protected void onServerStarted(MinecraftServer server) {
        // force load all node registries
        server.getAllLevels().forEach(level -> {
            var registry = NodeRegistry.of(level);
            LOGGER.info("Loaded {} nodes in level {}", registry.size(), level.dimension().identifier());
        });
    }

    protected void onServerTick(MinecraftServer server) {
        while (!serverTasks.isEmpty()) {
            var task = serverTasks.poll();
            if (task != null) {
                task.run();
            }
        }
        if (server.getTickCount() % connectionTick == 0) {
            server.getAllLevels().forEach(level -> NodeRegistry.of(level).forSources(SourceNode::connectionTick));
        }
        if (SculkRadio.redstoneEnabled && server.getTickCount() % redstoneTick == 0) {
            server.getAllLevels().forEach(level -> NodeRegistry.of(level).forSources(SourceNode::redstoneTick));
        }
        if (server.getTickCount() % visualsTick == 0) {
            server.getAllLevels().forEach(level -> NodeRegistry.of(level).forSources(SourceNode::visualsTick));
        }
    }

    abstract protected boolean isVoiceChatInstalled();

    abstract protected boolean isFuzzyConfigInstalled();

    protected void onInitialize() {

        if (isVoiceChatInstalled()) {
            voiceChatInstalled = true;
        }
        if (isFuzzyConfigInstalled()) {
            FzzyConfig.firstLoad();
        } else {
            LOGGER.info("Fzzy Config not found, using default settings.");
            FzzyConfig.reloadConfig();
        }
    }
}
