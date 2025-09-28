package nl.gjorgdy.sculk_radio.objects;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.NodeRegistry;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

import java.util.Set;
import java.util.function.Consumer;

public class SourceNode extends CalibratedNode {

    private final Set<Node> receivers = new ObjectArraySet<>(8);

    public SourceNode(ServerWorld world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public void stop(Consumer<Node> callback) {
        receivers.forEach(n -> n.stop(callback));
        receivers.clear();
        isPlaying = false;
    }

    @Override
    public void play(Consumer<Node> callback) {
        updateFrequency();
        NodeRegistry.INSTANCE.connectNodes(this);
        receivers.forEach(n -> n.play(callback));
        isPlaying = true;
    }

    @Override
    public void playTick() {
        if (isPlaying) {
            ParticleUtils.spawnShriekerParticles(this);
            receivers.forEach(n -> {
                if (!n.isConnected()) return;
                ParticleUtils.spawnVibrationParticles(this, n);
                n.playTick();
            });
        }
    }

    @Override
    public boolean isConnected() {
        return !receivers.isEmpty();
    }

    @Override
    public boolean connect(Node node) {
        if (isPlaying || node == this) return false;
        if (receivers.size() < 8) {
            return receivers.add(node);
        }
        return false;
    }

}
