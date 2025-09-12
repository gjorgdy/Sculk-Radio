package nl.gjorgdy.sculk_radio.objects;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public abstract class CalibratedNode extends Node {

    private int frequency;

    public CalibratedNode(BlockPos pos, ServerWorld world) {
        super(pos, world);
        ServerLifecycleEvents.SERVER_STARTED
            .register(s -> updateFrequency());
    }

    public int getFrequency() {
        return frequency;
    }

    public void updateFrequency() {
        this.frequency = getWorld().getEmittedRedstonePower(getPos().north(), Direction.NORTH)
                + getWorld().getEmittedRedstonePower(getPos().east(), Direction.EAST)
                + getWorld().getEmittedRedstonePower(getPos().south(), Direction.SOUTH)
                + getWorld().getEmittedRedstonePower(getPos().west(), Direction.WEST);
        System.out.println("setting frequency to " + frequency + ".");
    }

}
