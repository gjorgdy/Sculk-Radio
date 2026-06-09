package nl.gjorgdy.sculk_radio.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.connections.SculkStream;
import nl.gjorgdy.sculk_radio.interfaces.IStreamTransmitter;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class RadioNode extends Node implements IStreamTransmitter {

	private final SculkStream stream;

	public RadioNode(ServerLevel world, BlockPos pos) {
		super(world, pos);
		stream = new SculkStream(this);
		streams.put(stream, this);
	}

	public void play(Consumer<SculkStream.StreamConsumerNode> startConsumer, Consumer<SculkStream.StreamConsumerNode> stopConsumer) {
		stream(startConsumer, stopConsumer, false);
	}

	public void stream(Consumer<SculkStream.StreamConsumerNode> connectConsumer, Consumer<SculkStream.StreamConsumerNode> disconnectConsumer, boolean persistent) {
		stream.start(connectConsumer, disconnectConsumer, persistent);
	}

	public void tick() {
		stream.tick();
		ParticleUtils.spawnShriekerParticles(this);
	}

	public void stop() {
		stream.stop();
		// tick to deactivate visual states
		tick();
	}

	@Override
	public @Nullable SculkStream getStream() {
		return stream;
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
