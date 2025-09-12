package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public abstract class Node {

    protected boolean isPlaying = false;
    private final BlockPos pos;
    private final ServerWorld world;

    public Node(BlockPos pos, ServerWorld world) {
        this.pos = pos;
        this.world = world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void stop(Consumer<Node> callback) {
        isPlaying = false;
        callback.accept(this);
    }

    public void play(Consumer<Node> callback) {
        isPlaying = true;
        callback.accept(this);
    }

    public void playTick(Consumer<Node> callback) {
        if (isPlaying) {
            callback.accept(this);
        }
    }

}
