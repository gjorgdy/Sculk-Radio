package nl.gjorgdy.sculk_radio.objects;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.NodeRegistry;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

public class SourceNode extends CalibratedNode {

    private final Set<Node> speakers = new ObjectArraySet<>();

    public SourceNode(ServerWorld world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public void stop(Consumer<Node> callback) {
        speakers.forEach(callback);
        isPlaying = false;
        speakers.clear();
    }

    @Override
    public void play(Consumer<Node> callback) {
        updateFrequency();
        NodeRegistry.INSTANCE.connectNodes(this);
        speakers.forEach(callback);
        isPlaying = true;
    }

    @Override
    public void playTick() {
        if (isPlaying) {
            ParticleUtils.spawnShriekerParticles(this);
            speakers.forEach(n -> {
                if (n instanceof ReceiverNode) {
                    ParticleUtils.spawnVibrationParticles(this, n);
                }
                n.playTick();
            });
        }
    }

    public boolean connect(Node... speaker) {
        if (isPlaying) return false;
        speakers.addAll(Arrays.asList(speaker));
        return true;
    }

}
