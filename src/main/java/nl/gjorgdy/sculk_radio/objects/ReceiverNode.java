package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

public class ReceiverNode extends CalibratedNode {

    public ReceiverNode(BlockPos pos, ServerWorld world) {
        super(pos, world);
    }

    @Override
    public void playTick() {
        ParticleUtils.spawnNoteParticles(this);
    }

}
