package nl.gjorgdy.sculk_radio.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.streams.Stream;
import nl.gjorgdy.sculk_radio.streams.StreamState;

import java.util.function.Function;

public abstract class SourceNode extends Node {

	private Stream stream;

	public SourceNode(BlockPos pos) {
		super(pos);
	}

	public void start(Function<SourceNode, Stream> streamFactory) {
		stream = streamFactory.apply(this);
		stream.connectionTick();
		stream.start();
	}

	public void connectionTick() {
		if (stream == null) return;
		stream.connectionTick();
	}

	public void particleTick(ServerLevel level) {
		if (stream == null) return;
		stream.particleTick(level);
	}

	public void stop() {
		if (stream == null) return;
		stream.stop();
	}

	public StreamState getState() {
		if (stream == null) return StreamState.IDLE;
		return stream.getState();
	}

	@Override
	public boolean canTransmit() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}
}
