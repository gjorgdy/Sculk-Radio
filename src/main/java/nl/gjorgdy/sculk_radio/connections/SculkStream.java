package nl.gjorgdy.sculk_radio.connections;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.nodes.Node;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SculkStream {

	private final Node sourceNode;
	private Consumer<StreamConsumerNode> listenConsumer;
	private Consumer<StreamConsumerNode> stopListeningConsumer;
	private boolean isPersistent;

	private final Set<StreamConsumerNode> listeners = new HashSet<>();
	private boolean active = false;

	public SculkStream(Node sourceNode) {
		this.sourceNode = sourceNode;
	}

	/**
	 * Start this stream
	 */
	public void start(@NotNull Consumer<StreamConsumerNode> listenConsumer, @NotNull Consumer<StreamConsumerNode> stopListeningConsumer, boolean isPersistent) {
		if (active) stop();
		// configure stream
		this.listenConsumer = listenConsumer;
		this.stopListeningConsumer = stopListeningConsumer;
		this.isPersistent = isPersistent;
		// init listeners
		active = true;
		listeners.forEach(listenConsumer);
		tick();
	}

	public void tick() {
		listeners.forEach(l -> l.tick(this));
	}

	/**
	 * Stop this stream and stop all listeners
	 */
	public void stop() {
		active = false;
		tick();
		listeners.forEach(stopListeningConsumer);
	}

	/**
	 * Start listening to this stream
	 * <p>
	 * If the stream has already started, can only join if persistent
	 *
	 * @param node the node to start listen
	 */
	private void listen(StreamConsumerNode node) {
		if (active && !isPersistent) return;
		boolean added = listeners.add(node);
		if (active) {
			listenConsumer.accept(node);
			node.tick(this);
		}
	}

	/**
	 * Stop listening to this stream
	 * @param node the node to stop listening
	 */
	private void stopListening(StreamConsumerNode node) {
		if (!active) return;
		stopListeningConsumer.accept(node);
		listeners.remove(node);
		node.tick(this);
	}

	public boolean isActive() {
		return active;
	}

	public static abstract class StreamConsumerNode extends Node {

		public StreamConsumerNode(ServerLevel level, BlockPos pos) {
			super(level, pos);
		}

		@Override
		protected void onReceive(SculkStream receivedStream) {
			super.onReceive(receivedStream);
			receivedStream.listen(this);
		}

		@Override
		protected void onStopReceive(SculkStream receivedStream) {
			super.onStopReceive(receivedStream);
			receivedStream.stopListening(this);
		}

		public void tick(SculkStream stream) {
			Set<Node> visited = new HashSet<>();
			Node to = this;
			Node from;
			do {
				from = to.receivesStreamFrom(stream);
				if (visited.contains(from)) break;
				if (from != null) {
					if (stream.isActive()) {
						ParticleUtils.activateSensor(to);
					} else {
						ParticleUtils.deactivateSensor(to);
					}
					ParticleUtils.spawnVibrationParticles(from, to);
					to = from;
				}
				visited.add(from);
			} while (to != stream.sourceNode);
			ParticleUtils.spawnNoteParticles(this);
		}
	}

}
