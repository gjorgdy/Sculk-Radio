package nl.gjorgdy.sculk_radio.connections;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.nodes.Node;
import nl.gjorgdy.sculk_radio.nodes.RelayNode;
import nl.gjorgdy.sculk_radio.nodes.SpeakerNode;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SculkStream {

	private final Node sourceNode;
	private final Consumer<ListeningNode> listenConsumer;
	private final Consumer<ListeningNode> stopListeningConsumer;
	private final boolean isPersistent;

	private final Set<ListeningNode> listeners = new HashSet<>();
	private boolean started = false;
	private boolean stopped = false;

	public SculkStream(Node sourceNode, @NotNull Consumer<ListeningNode> listenConsumer, @NotNull Consumer<ListeningNode> stopListeningConsumer, boolean isPersistent) {
		this.sourceNode = sourceNode;
		this.listenConsumer = listenConsumer;
		this.stopListeningConsumer = stopListeningConsumer;
		this.isPersistent = isPersistent;
	}

	public void forListeners(Consumer<ListeningNode> consumer) {
		listeners.forEach(consumer);
	}

	/**
	 * Start this stream
	 */
	public void start() {
		started = true;
		listeners.forEach(listenConsumer);
	}

	public void tick() {
		listeners.forEach(l -> l.tick(this));
	}

	/**
	 * Stop this stream and stop all listeners
	 */
	public void stop() {
		stopped = true;
		listeners.forEach(stopListeningConsumer);
	}

	/**
	 * Start listening to this stream
	 * <p>
	 * If the stream has already started, can only join if persistent
	 * @param node the node to start listen
	 */
	private boolean listen(ListeningNode node) {
		if (stopped || (started && !isPersistent)) return false;
		boolean added = listeners.add(node);
		if (started) {
			listenConsumer.accept(node);
			node.tick(this);
		}
		return added;
	}

	/**
	 * Stop listening to this stream
	 * @param node the node to stop listening
	 */
	private void stopListening(ListeningNode node) {
		if (stopped) return;
		stopListeningConsumer.accept(node);
		listeners.remove(node);
		node.tick(this);
	}

	public boolean isActive() {
		return started && !stopped;
	}

	public static abstract class ListeningNode extends Node {

		private final Set<SculkStream> streams;

		public ListeningNode(ServerLevel level, BlockPos pos) {
			streams = new HashSet<>();
			super(level, pos);
		}

		public void listenTo(SculkStream stream) {
			if (stream.listen(this)) {
				this.streams.add(stream);
			}
		}

		@Override
		public void afterRemove() {
			stopListening();
			super.afterRemove();
		}

		public void stopListening() {
			streams.forEach(stream -> {
				stream.stopListening(this);
				// disable visual path
				var cluster = getCluster();
				if (cluster == null) return;
				cluster.path((_, to) -> {
					if (to instanceof SpeakerNode || to instanceof RelayNode) {
						ParticleUtils.deactivateSensor(to);
					}
				}, stream.sourceNode, this);
			});
			streams.clear();
		}

		public boolean isListening() {
			return streams.stream().anyMatch(stream -> stream != null && stream.isActive());
		}

		public void tick(SculkStream stream) {
			getCluster().path((from, to) -> {
				ParticleUtils.spawnVibrationParticles(from, to);
				if (to instanceof SpeakerNode || to instanceof RelayNode) {
					if (stream.isActive()) {
						ParticleUtils.activateSensor(to);
					} else {
						ParticleUtils.deactivateSensor(to);
					}
				}
			}, stream.sourceNode, this);
			ParticleUtils.spawnNoteParticles(this);
		}
	}

}
