package nl.gjorgdy.sculk_radio.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class RadioNode extends Node {

	private boolean isPlaying;

	private final Set<SpeakerNode> listeners = new HashSet<>();
	private Consumer<SpeakerNode> stopConsumer;

	public RadioNode(ServerLevel world, BlockPos pos) {
		super(world, pos);
	}

	public void play(Consumer<SpeakerNode> startConsumer, Consumer<SpeakerNode> stopConsumer) {
		if (isPlaying) stop();
		this.isPlaying = true;
		this.stopConsumer = stopConsumer;

		getCluster().forNodes(n -> {
			if (n instanceof SpeakerNode speaker) {
				startConsumer.accept(speaker);
				listeners.add(speaker);
			}
		});
	}

	public void tick() {
		if (isPlaying) {
			listeners.forEach(speaker -> {
				getCluster().path((from, to) -> {
					ParticleUtils.spawnVibrationParticles(from, to);
					if (to instanceof SpeakerNode || to instanceof RelayNode) {
						ParticleUtils.activateSensor(to);
					}
				}, this, speaker);
				ParticleUtils.spawnNoteParticles(speaker);
			});
		}
		ParticleUtils.spawnShriekerParticles(this);
	}

	public void stop() {
		if (stopConsumer != null) listeners.forEach(stopConsumer);
		listeners.forEach(speaker -> {
			getCluster().path((_, to) -> {
				if (to instanceof SpeakerNode || to instanceof RelayNode) {
					ParticleUtils.deactivateSensor(to);
				}
			}, this, speaker);
			ParticleUtils.spawnNoteParticles(speaker);
		});
		listeners.clear();

		isPlaying = false;
		this.stopConsumer = null;
	}

	public boolean isPlaying() {
		return isPlaying;
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
