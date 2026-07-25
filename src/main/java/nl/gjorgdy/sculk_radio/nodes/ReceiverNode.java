package nl.gjorgdy.sculk_radio.nodes;

import net.minecraft.core.BlockPos;

public abstract class ReceiverNode extends Node {

	private int weakRedstonePower = 0;
	private int strongRedstonePower = 0;

	public ReceiverNode(BlockPos pos) {
        super(pos);
    }

    @Override
    public boolean canReceive() {
		return true;
    }

	@Override
	public boolean canTransmit() {
		return false;
	}

	public void setWeakRedstonePower(int weakRedstonePower) {
		this.weakRedstonePower = weakRedstonePower;
	}

	public void setStrongRedstonePower(int strongRedstonePower) {
		this.strongRedstonePower = strongRedstonePower;
	}

	public final int getWeakRedstonePower() {
		return weakRedstonePower;
	}

	public int getStrongRedstonePower() {
		return strongRedstonePower;
	}
}