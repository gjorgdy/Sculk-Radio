package nl.gjorgdy.sculk_radio.objects;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Set;
import java.util.function.Consumer;

public abstract class Node {

    private final BlockPos pos;
    private final ServerWorld world;

    protected final Set<Node> receivers = new ObjectArraySet<>(8);
    protected Node transmitter = null;

    private Consumer<Node> disconnectCallback;
    private Consumer<Node> tickCallback;

    public Node(ServerWorld world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }

    public ServerWorld getWorld() {
        return world;
    }

    protected void internalInitialize(Consumer<Node> connectCallback, Consumer<Node> disconnectCallback, Consumer<Node> tickCallback) {
        this.disconnectCallback = disconnectCallback;
        this.tickCallback = tickCallback;
        connectCallback.accept(this);
    }

    public boolean connect(Node toNode) {
        toNode.transmitter = this;
        return true;
    }

    public boolean isConnected() {
        return transmitter != null;
    }

    public abstract boolean isActive();

    public void disconnect() {
        if (!isConnected()) return;
        // disconnect transmitter
        transmitter = null;
        // run disconnect callback
        if (disconnectCallback != null) disconnectCallback.accept(this);
    }

    public final void tick() {
        if (isConnected()) {
            if (tickCallback != null) tickCallback.accept(this);
            internalTick();
        }
    }

    protected abstract void internalTick();

}
