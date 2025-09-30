package nl.gjorgdy.sculk_radio;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.interfaces.NodeContainer;
import nl.gjorgdy.sculk_radio.objects.Node;

import java.util.function.Consumer;

public class SculkRadio {

    public static void RunIfServerActive(Runnable runnable) {
        ServerLifecycleEvents.SERVER_STARTED.register(s -> runnable.run());
    }

    public static class API {
        /**
         * Run a callback on a jukebox and connected speakers
         *
         * @param world        the world the jukebox is in
         * @param pos          the jukebox's position
         * @param callback     the callback to run on the jukebox and speakers
         * @param stopCallback the callback to run on the jukebox and speakers when the sound stops playing
         * @return true if the callback was run, false if the callback could not be played.
         */
        public static boolean play(ServerWorld world, BlockPos pos, Consumer<Node> callback, Consumer<Node> stopCallback) {
            var blockEntity = world.getBlockEntity(pos.up());
            if (blockEntity instanceof NodeContainer nc && nc.sculkRadio$getNode() instanceof Node node) {
                node.play(callback, stopCallback);
                return true;
            }
            return false;
        }

        /**
         * Stop a jukebox and connected speakers
         *
         * @param world the world the jukebox is in
         * @param pos   the jukebox's position
         * @return true if the sound was stopped, false if the sound could not be stopped.
         */
        public static boolean stop(ServerWorld world, BlockPos pos) {
            var blockEntity = world.getBlockEntity(pos.up());
            if (blockEntity instanceof NodeContainer nc && nc.sculkRadio$getNode() instanceof Node node) {
                node.stop();
                return true;
            }
            return false;
        }
    }
}
