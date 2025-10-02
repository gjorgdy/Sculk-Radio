package nl.gjorgdy.sculk_radio;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.interfaces.ISculkRadioApi;
import nl.gjorgdy.sculk_radio.objects.Node;

import java.util.function.Consumer;

public class SculkRadio {

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
        public boolean connect(ServerWorld world, BlockPos pos, Consumer<Node> connectCallback, Consumer<Node> disconnectCallback) {
            var node = getNode(world, pos);
            if (node != null) {
                node.play(connectCallback, disconnectCallback);
                return true;
            }
            return false;
        }

        @Override
        public boolean disconnect(ServerWorld world, BlockPos pos) {
            var node = getNode(world, pos);
            if (node != null) {
                node.stop();
                return true;
            }
            return false;
        }

        @Override
        public boolean tick(ServerWorld world, BlockPos pos) {
            var node = getNode(world, pos);
            if (node != null) {
                node.playTick();
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
