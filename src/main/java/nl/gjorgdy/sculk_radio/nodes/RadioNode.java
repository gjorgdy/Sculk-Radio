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

	public void play(Consumer<SpeakerNode> startConsumer, Consumer<SpeakerNode> stopConsumer) {
		stream(startConsumer, stopConsumer, false);
	}

	public void stream(Consumer<SpeakerNode> connectConsumer, Consumer<SpeakerNode> disconnectConsumer, boolean persistent) {
		stream(new SculkStream(
			n -> {
				if (n instanceof SpeakerNode speaker) {
					connectConsumer.accept(speaker);
					ParticleUtils.activateSensor(speaker);
				}
			},
			n -> {
				if (n instanceof SpeakerNode speaker) {
					disconnectConsumer.accept(speaker);
					ParticleUtils.deactivateSensor(speaker);
				}
			},
			persistent
		));
	}

	public void stream(SculkStream stream) {
		if (this.stream != null) stop();
		this.stream = stream;
		getCluster().announceStream(this);
		stream.start();
	}

	public void tick() {
		if (stream != null) {
			stream.forListeners(node -> {
				if (!(node instanceof SpeakerNode speaker)) return;
				getCluster().path((from, to) -> {
					ParticleUtils.spawnVibrationParticles(from, to);
					if (to instanceof SpeakerNode || to instanceof RelayNode) {
						if (stream.isActive()) {
							ParticleUtils.activateSensor(to);
						} else {
							ParticleUtils.deactivateSensor(to);
						}
					}
				}, this, speaker);
				ParticleUtils.spawnNoteParticles(speaker);
			});
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
