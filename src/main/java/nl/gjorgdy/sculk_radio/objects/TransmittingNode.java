package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

import java.util.function.Consumer;

public abstract class TransmittingNode extends Node {

    public TransmittingNode(ServerWorld world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public void internalTick() {
        // remove inactive receivers
        receivers.removeIf(n -> {
            if (!n.isActive()) {
                n.disconnect();
                return true;
            }
            return false;
        });
        // tick all connected receivers
        receivers.forEach(n -> {
            // vibration particles from this to the receiving node
            ParticleUtils.spawnVibrationParticles(this, n);
            // tick the receiving node self
            n.tick();
        });
    }

    @Override
    protected void internalInitialize(Consumer<Node> connectCallback, Consumer<Node> disconnectCallback, Consumer<Node> tickCallback) {
        receivers.forEach(node -> node.internalInitialize(connectCallback, disconnectCallback, tickCallback));
    }

    @Override
    public void disconnect() {
        receivers.forEach(Node::disconnect);
        receivers.clear();
        super.disconnect();
    }

    @Override
    public boolean connect(Node toNode) {
        if (toNode.isConnected() || toNode == this) return false;
        if (receivers.size() < 8) {
            boolean connected = receivers.add(toNode);
            if (connected) toNode.transmitter = this;
            return connected;
        }
        return false;
    }

    @Override
    public boolean isConnected() {
        return !receivers.isEmpty() && super.isConnected();
    }

    @Override
    public boolean isActive() {
        return !receivers.isEmpty() && receivers.stream().anyMatch(Node::isActive);
    }

}
