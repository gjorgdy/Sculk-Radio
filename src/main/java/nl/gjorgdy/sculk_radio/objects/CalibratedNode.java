package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class CalibratedNode extends Node {

    private int frequency;

    public CalibratedNode(BlockPos pos, ServerWorld world) {
        super(pos, world);
    }

    @Override
    public void playTick() {

    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

}
