package nl.gjorgdy.sculk_radio.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.connections.SculkStream;
import nl.gjorgdy.sculk_radio.interfaces.IStreamTransmitter;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class RadioNode extends Node implements IStreamTransmitter {

	private @Nullable SculkStream stream;

	public RadioNode(ServerLevel world, BlockPos pos) {
		super(world, pos);
	}

	public void play(Consumer<SculkStream.ListeningNode> startConsumer, Consumer<SculkStream.ListeningNode> stopConsumer) {
		stream(startConsumer, stopConsumer, false);
	}

	public void stream(Consumer<SculkStream.ListeningNode> connectConsumer, Consumer<SculkStream.ListeningNode> disconnectConsumer, boolean persistent) {
		stream(
			new SculkStream(
		        this,
				connectConsumer,
				disconnectConsumer,
				persistent
			)
		);
	}

	public void stream(SculkStream stream) {
		if (this.stream != null) stop();
		this.stream = stream;
		getCluster().announceStream(this);
		stream.start();
	}

	public void tick() {
		if (stream != null) {
			stream.tick();
		}
		ParticleUtils.spawnShriekerParticles(this);
	}

	public void stop() {
		if (stream == null) return;
		stream.stop();
		// tick to deactivate visual states
		tick();
		stream = null;
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
