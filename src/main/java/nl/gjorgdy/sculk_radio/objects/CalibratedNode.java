package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import nl.gjorgdy.sculk_radio.SculkRadio;

public abstract class CalibratedNode extends Node {

    protected int frequency;

    public CalibratedNode(ServerWorld world, BlockPos pos) {
        super(world, pos);
        SculkRadio.RunIfServerActive(this::updateFrequency);
    }

    @Override
    public int getFrequency() {
        return frequency;
    }

    public void updateFrequency() {
        this.frequency = getWorld().getEmittedRedstonePower(getPos().north(), Direction.NORTH)
                + getWorld().getEmittedRedstonePower(getPos().east(), Direction.EAST)
                + getWorld().getEmittedRedstonePower(getPos().south(), Direction.SOUTH)
                + getWorld().getEmittedRedstonePower(getPos().west(), Direction.WEST);
    }

}
