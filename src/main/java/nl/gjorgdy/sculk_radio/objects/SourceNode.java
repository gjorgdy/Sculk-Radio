package nl.gjorgdy.sculk_radio.objects;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.NodeRegistry;

import java.util.Set;
import java.util.function.Consumer;

public class SourceNode extends Node {

    private final Set<Node> speakers = new ObjectArraySet<>();

    public SourceNode(BlockPos pos) {
        super(pos);
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public void stop(Consumer<Node> callback) {
        if (!speakers.isEmpty() && isPlaying) {
            speakers.forEach(callback);
        }
        isPlaying = false;
        speakers.clear();
    }

    @Override
    public void play(Consumer<Node> callback) {
        speakers.add(NodeRegistry.INSTANCE.getClosestReceiver(this.getPos()));
        speakers.forEach(callback);
        isPlaying = true;
    }

    @Override
    public void playTick(Consumer<Node> callback) {
        if (isPlaying) {
            speakers.forEach(callback);
        }
    }

}
