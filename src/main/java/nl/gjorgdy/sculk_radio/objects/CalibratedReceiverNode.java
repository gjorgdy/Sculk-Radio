package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CalibratedSculkSensorBlock;
import nl.gjorgdy.sculk_radio.interfaces.ICalibrated;

public class CalibratedReceiverNode extends ReceiverNode implements ICalibrated {

    private int frequency;

    public CalibratedReceiverNode(ServerLevel world, BlockPos pos) {
        super(world, pos);
        onInitialize();
    }

    @Override
    public int getFrequency() {
        updateFrequency();
        return frequency;
    }

    @Override
    public void updateFrequency() {
        var direction = getWorld().getBlockState(getPos()).getValue(CalibratedSculkSensorBlock.FACING);
        this.frequency = getWorld().getDirectSignal(
            getPos().relative(direction.getOpposite()),
            direction.getOpposite()
        );
    }

}
