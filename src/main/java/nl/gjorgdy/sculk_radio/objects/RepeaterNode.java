package nl.gjorgdy.sculk_radio.objects;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

import java.util.Set;
import java.util.function.Consumer;

public class RepeaterNode extends Node {

    private final Set<Node> receivers = new ObjectArraySet<>(8);

    public RepeaterNode(ServerWorld world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getFrequency() {
        return 0;
    }

    @Override
    public void playTick() {
        ParticleUtils.activateSensor(this);
        receivers.forEach(n -> {
            if (!n.isConnected()) return;
            ParticleUtils.spawnVibrationParticles(this, n);
            n.playTick();
        });
    }

    @Override
    public void play(Consumer<Node> callback) {
        receivers.forEach(node -> node.play(callback));
    }

    @Override
    public void stop(Consumer<Node> callback) {
        receivers.forEach(node -> node.stop(callback));
        ParticleUtils.deactivateSensor(this);
        receivers.clear();
    }

    @Override
    public boolean connect(Node node) {
        if (node == this) return false;
        if (receivers.size() < 8) {
            return receivers.add(node);
        }
        return false;
    }

    @Override
    public boolean isConnected() {
        return !receivers.isEmpty();
    }
}
