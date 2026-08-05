package nl.gjorgdy.sculk_radio.objects.nodes.abstracts;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.objects.nodes.AntennaNode;
import nl.gjorgdy.sculk_radio.objects.streams.Stream;
import nl.gjorgdy.sculk_radio.objects.streams.StreamState;
import nl.gjorgdy.sculk_radio.utils.VisualUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class ReceiverNode extends Node {

	private final Set<Stream> streams;

	public ReceiverNode(BlockPos pos) {
        super(pos);
		streams = new HashSet<>(2);
    }

	public final void addStream(Stream stream) {
		streams.add(stream);
	}

	public final void removeStream(Stream stream) {
		streams.remove(stream);
	}

	public Set<Stream> getStreams() {
		streams.removeIf(s -> s.getState() == StreamState.STOPPED);
		return Collections.unmodifiableSet(streams);
	}

    @Override
    public boolean canReceive() {
		return true;
    }

	@Override
	public boolean canTransmit() {
		return false;
	}

	protected void internalInit() {
		SculkRadio.scheduleNextTick(this::updateNeighbours);
	}

	@Override
	public void pulseNeighbours() {
		getNeighbours().forEach(
			neighbour -> VisualUtils.spawnVibrationParticles(level, neighbour.getPos(), this.getPos())
		);
	}

	public void updateNeighbours() {
		if (!isLoaded()) return;
		level.updateNeighborsAt(pos.below(), level.getBlockState(pos).getBlock());
	}

	public final int getRedstoneSignal() {
		if (streams.isEmpty()) return 0;
		return getStreams().stream().mapToInt(Stream::getRedstoneSignal).max().orElse(0);
	}

	public final int getAnalogRedstoneSignal() {
		if (streams.isEmpty()) return 0;
		return getStreams().stream().mapToInt(Stream::getAnalogRedstoneSignal).max().orElse(0);
	}
}