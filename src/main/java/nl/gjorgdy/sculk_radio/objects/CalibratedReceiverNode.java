package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.block.CalibratedSculkSensorBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

public class CalibratedReceiverNode extends CalibratedNode {

    public CalibratedReceiverNode(ServerWorld world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public void playTick() {
        ParticleUtils.activateSensor(this);
        ParticleUtils.spawnNoteParticles(this);
    }

    @Override
    public void updateFrequency() {
        var direction = getWorld().getBlockState(getPos()).get(CalibratedSculkSensorBlock.FACING);
        this.frequency = getWorld().getEmittedRedstonePower(
                getPos().offset(direction.getOpposite()),
                direction.getOpposite()
        );
        System.out.println("setting frequency to " + frequency + ".");
    }
}
