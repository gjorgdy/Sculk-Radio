package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

public class ReceiverNode extends Node {

    private Node sourceNode = null;

    public ReceiverNode(ServerWorld world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getFrequency() {
        return 0;
    }

    @Override
    public void playTick() {
        ParticleUtils.activateSensor(this);
        ParticleUtils.spawnNoteParticles(this);
    }

    @Override
    public void stop() {
        super.stop();
        ParticleUtils.deactivateSensor(this);
        sourceNode = null;
    }

    @Override
    public boolean isConnected() {
        return sourceNode != null;
    }

    @Override
    public boolean connect(Node node) {
        if (isPlaying || node == this) return false;
        if (!isConnected())
            sourceNode = node;
        return isConnected();
    }

}
