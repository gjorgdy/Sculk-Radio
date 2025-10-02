package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.block.CalibratedSculkSensorBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.interfaces.ICalibrated;

public class CalibratedReceiverNode extends ReceiverNode implements ICalibrated {

    private int frequency;

    public CalibratedReceiverNode(ServerWorld world, BlockPos pos) {
        super(world, pos);
        onInitialize();
    }

    @Override
    public int getFrequency() {
        return frequency;
    }

    @Override
    public void updateFrequency() {
        var direction = getWorld().getBlockState(getPos()).get(CalibratedSculkSensorBlock.FACING);
        this.frequency = getWorld().getEmittedRedstonePower(
                getPos().offset(direction.getOpposite()),
                direction.getOpposite()
        );
    }

}
