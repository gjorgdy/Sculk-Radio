package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

public class ReceiverNode extends Node {

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

}
