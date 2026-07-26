package nl.gjorgdy.sculk_radio.objects.nodes.abstracts;

import net.minecraft.core.BlockPos;

import java.util.Optional;

public abstract class ReceiverNode extends Node {

	private int weakRedstonePower = 0;
	private int strongRedstonePower = 0;

	public ReceiverNode(BlockPos pos) {
        super(pos);
    }

	protected ReceiverNode(BlockPos pos, int weakRedstonePower, int strongRedstonePower) {
		super(pos);
		this.weakRedstonePower = weakRedstonePower;
		this.strongRedstonePower = strongRedstonePower;
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

	public final Optional<Integer> getWeakRedstonePower() {
		return weakRedstonePower == 0 ? Optional.empty() : Optional.of(weakRedstonePower);
	}

	public Optional<Integer> getStrongRedstonePower() {
		return strongRedstonePower == 0 ? Optional.empty() : Optional.of(strongRedstonePower);
	}
}