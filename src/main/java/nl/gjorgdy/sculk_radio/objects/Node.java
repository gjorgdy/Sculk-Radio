package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public abstract class Node {

    protected boolean isPlaying = false;
    private final BlockPos pos;
    private final ServerWorld world;

    public Node(ServerWorld world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    public abstract int getFrequency();

    public BlockPos getPos() {
        return pos;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void play(Consumer<Node> callback) {
        isPlaying = true;
        callback.accept(this);
    }

    public void stop(Consumer<Node> callback) {
        isPlaying = false;
        callback.accept(this);
    }

    public abstract void playTick();

    public abstract boolean isConnected();

    public abstract boolean connect(Node node);

}
