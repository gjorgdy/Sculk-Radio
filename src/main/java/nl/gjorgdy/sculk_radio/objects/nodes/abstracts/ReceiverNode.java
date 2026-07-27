package nl.gjorgdy.sculk_radio.objects.nodes.abstracts;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public abstract class ReceiverNode extends Node {

	private int analogRedstoneSignal = 0;
	private int redstoneSignal = 0;

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

	public void setAnalogRedstoneSignal(int analogRedstoneSignal) {
		if (this.analogRedstoneSignal != analogRedstoneSignal) {
			this.analogRedstoneSignal = analogRedstoneSignal;
			updateNeighbours();
		}
	}

	@Override
	public void init(ServerLevel level) {
		super.init(level);
		updateNeighbours();
	}

	public void setRedstoneSignal(int redstoneSignal) {
		if (this.redstoneSignal != redstoneSignal) {
			this.redstoneSignal = redstoneSignal;
			updateNeighbours();
		}
	}

	public void updateNeighbours() {
		if (!isLoaded()) return;
		level.updateNeighborsAt(pos.below(), level.getBlockState(pos).getBlock());
	}

	public final int getAnalogSignal() {
		return analogRedstoneSignal;
	}

	public int getOwnSignal() {
		return redstoneSignal;
	}
}