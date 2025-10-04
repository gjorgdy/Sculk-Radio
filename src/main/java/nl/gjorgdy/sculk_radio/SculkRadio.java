package nl.gjorgdy.sculk_radio;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.interfaces.ISculkRadioApi;
import nl.gjorgdy.sculk_radio.objects.Node;
import nl.gjorgdy.sculk_radio.objects.SourceNode;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class SculkRadio {

    public static Logger LOGGER = Logger.getLogger("Sculk Radio");

    public static void RunIfServerActive(Runnable runnable) {
        ServerLifecycleEvents.SERVER_STARTED.register(s -> runnable.run());
    }

    private static API apiInstance = null;

    public static ISculkRadioApi api() {
        if (apiInstance == null) {
            apiInstance = new API();
        }
        return apiInstance;
    }

    public static class API implements ISculkRadioApi {

        @Override
        public boolean isRadio(ServerWorld world, BlockPos pos) {
            return getNode(world, pos) != null;
        }

        @Override
        public boolean connect(ServerWorld world, BlockPos pos, Consumer<Node> connectCallback, Consumer<Node> disconnectCallback, Consumer<Node> tickCallback) {
            var node = getNode(world, pos);
            if (node instanceof SourceNode sn) {
                sn.initialize(connectCallback, disconnectCallback, tickCallback);
                return true;
            }
            return false;
        }

        @Override
        public boolean disconnect(ServerWorld world, BlockPos pos) {
            var node = getNode(world, pos);
            if (node != null) {
                node.disconnect();
                return true;
            }
            return false;
        }

        @Override
        public boolean tick(ServerWorld world, BlockPos pos) {
            var node = getNode(world, pos);
            if (node != null) {
                node.tick();
                return true;
            }
            return false;
        }

        private Node getNode(ServerWorld world, BlockPos pos) {
            var blockEntity = world.getBlockEntity(pos.up());
            if (blockEntity instanceof INodeContainer nc) {
                return nc.sculkRadio$getNode();
            }
            return null;
        }
    }
}
