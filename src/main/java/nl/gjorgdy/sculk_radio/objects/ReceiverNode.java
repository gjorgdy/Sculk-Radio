package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

public class ReceiverNode extends Node {

    public ReceiverNode(ServerWorld world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public void internalTick() {
        ParticleUtils.activateSensor(this);
        ParticleUtils.spawnNoteParticles(this);
    }

    @Override
    public void disconnect() {
        ParticleUtils.deactivateSensor(this);
        super.disconnect();
    }

    @Override
    public boolean isActive() {
        return isConnected();
    }

    @Override
    public boolean connect(Node toNode) {
        return false;
    }

}
