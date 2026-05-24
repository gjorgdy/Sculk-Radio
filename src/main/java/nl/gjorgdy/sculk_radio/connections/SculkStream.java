package nl.gjorgdy.sculk_radio.connections;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SculkStream {

	private final Consumer<? super SculkCluster.ClusterNode> listenConsumer;
	private final Consumer<? super SculkCluster.ClusterNode> stopListeningConsumer;
	private final boolean isPersistent;

	private final Set<SculkCluster.ClusterNode> listeners = new HashSet<>();
	private boolean started = false;
	private boolean stopped = false;

	public SculkStream(@NotNull Consumer<? super SculkCluster.ClusterNode> listenConsumer, @NotNull Consumer<? super SculkCluster.ClusterNode> stopListeningConsumer, boolean isPersistent) {
		this.listenConsumer = listenConsumer;
		this.stopListeningConsumer = stopListeningConsumer;
		this.isPersistent = isPersistent;
	}

	public void forListeners(Consumer<? super SculkCluster.ClusterNode> consumer) {
		listeners.forEach(consumer);
	}

	/**
	 * Start this stream
	 */
	public void start() {
		started = true;
		listeners.forEach(listenConsumer);
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
	public boolean listen(SculkCluster.ClusterNode node) {
		if (stopped || (started && !isPersistent)) return false;
		boolean added = listeners.add(node);
		if (started) listenConsumer.accept(node);
		return added;
	}

	/**
	 * Stop listening to this stream
	 * @param node the node to stop listening
	 */
	public boolean stopListening(SculkCluster.ClusterNode node) {
		if (stopped) return false;
		stopListeningConsumer.accept(node);
		return listeners.remove(node);
	}

	public boolean isActive() {
		return started && !stopped;
	}

}
