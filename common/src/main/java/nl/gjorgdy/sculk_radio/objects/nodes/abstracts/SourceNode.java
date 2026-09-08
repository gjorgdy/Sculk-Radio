package nl.gjorgdy.sculk_radio.objects.nodes.abstracts;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import nl.gjorgdy.sculk_radio.objects.streams.Stream;
import nl.gjorgdy.sculk_radio.objects.streams.StreamState;
import nl.gjorgdy.sculk_radio.utils.VisualUtils;

import java.util.function.Function;

public abstract class SourceNode<T extends Stream> extends Node {

	protected T stream;

	public SourceNode(BlockPos pos) {
		super(pos);
	}

	public void start(Function<SourceNode<T>, T> streamFactory) {
		stream = streamFactory.apply(this);
		stream.start();
	}

	public void redstoneTick() {
		if (stream == null) return;
		stream.redstoneTick();
	}

	public void connectionTick() {
		if (stream == null) return;
		stream.connectionTick();
	}

	@Override
	public void pulseNeighbours() {
		getNeighbours().forEach(
			neighbour -> VisualUtils.spawnVibrationParticles(level, this.getPos(), neighbour.getPos())
		);
	}

	public void visualsTick() {
		if (stream == null) return;
		stream.visualsTick(level);
	}

	public void stop() {
		if (stream == null) return;
		stream.stop();
	}

	public StreamState getState() {
		if (stream == null) return StreamState.IDLE;
		return stream.getState();
	}

	public int getRedstoneSignal() {
		if (level == null) return 0;
		return level.getBlockState(pos.below()).getOwnSignal(level, pos.below());
	}

	public int getAnalogRedstoneSignal() {
		if (level == null) return 0;
		return level.getBlockState(pos.below()).getAnalogOutputSignal(level, pos.below(), Direction.NORTH);
	}

	@Override
	public boolean canTransmit() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}

	@Override
	public void afterRemove() {
		super.afterRemove();
		if (stream != null) {
			stream.stop();
		}
	}
}
