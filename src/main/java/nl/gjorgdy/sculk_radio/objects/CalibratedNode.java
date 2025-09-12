package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.util.math.BlockPos;

public class CalibratedNode extends Node {

    private int frequency;

    public CalibratedNode(BlockPos pos) {
        super(pos);
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

}
