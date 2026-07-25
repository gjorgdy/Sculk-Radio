package nl.gjorgdy.sculk_radio.objects.streams;

import kotlin.Pair;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.RelayNode;
import nl.gjorgdy.sculk_radio.objects.NodePath;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Stream {

	private final Predicate<? super Node> isReceiver;
	private final Consumer<? super Node> connectConsumer;
	private final Consumer<? super Node> disconnectConsumer;
	// If nodes can 'connect' after playing has started
	private final boolean isLive;
	private final Node source;

	private final Set<Pair<Node, Node>> connections = new HashSet<>();
	private final Set<Node> listeners;
	private final Queue<Node> disconnectedNodes;
	private StreamState state = StreamState.IDLE;

	public Stream(
		Predicate<? super Node> isReceiver,
		Consumer<? super Node> connectConsumer,
		Consumer<? super Node> disconnectConsumer,
		Node source,
		boolean isLive
	) {
		this.isReceiver = isReceiver;
		this.connectConsumer = connectConsumer;
		this.disconnectConsumer = disconnectConsumer;
		this.source = source;
		this.isLive = isLive;
		listeners = new HashSet<>();
		disconnectedNodes = new LinkedList<>();
	}

	public void start() {
		if (state == StreamState.IDLE) {
			state = StreamState.ACTIVE;
		}
	}

	public void stop() {
		state = StreamState.STOPPED;
		forListeners(listener -> {
			disconnectedNodes.add(listener);
			onDisconnect(listener);
		});
		listeners.clear();
		forConnections((from, to) -> {
			disconnectedNodes.add(from);
			disconnectedNodes.add(to);
		});
		connections.clear();
	}

	public void particleTick(ServerLevel level) {
		if (state == StreamState.ACTIVE) {
			ParticleUtils.spawnShriekerParticles(level, source.getPos());
			forListeners((listener) -> listener.particleTick(level));
			forConnections((from, to) -> {
				if (from instanceof RelayNode) from.particleTick(level);
				ParticleUtils.spawnVibrationParticles(level, from.getPos(), to.getPos());
			});
		}
		while (!disconnectedNodes.isEmpty()) {
			Node node = disconnectedNodes.poll();
			if (node == null) continue;
			ParticleUtils.deactivateSensor(level, node.getPos());
		}
	}

	public void connectionTick() {
		if (state == StreamState.STOPPED) return;
		Set<Node> newListeners = new HashSet<>();
		Set<Pair<Node, Node>> newConnections = new HashSet<>();
		Set<Node> visited = new HashSet<>();
		visited.add(source);
		connectNeighbours(visited, NodePath.of(source), newListeners, newConnections, source);
		// add new connections
		connections.addAll(newConnections);
		// connect new listeners
		newListeners.forEach((newListener) -> {
			if (!listeners.contains(newListener)) {
				connect(newListener, NodePath.of(newListener));
			}
		});
		// remove old connections
		connections.removeIf(pair -> {
			boolean disconnect = !newConnections.contains(pair);
			if (disconnect) {
				disconnectedNodes.add(pair.getFirst());
				disconnectedNodes.add(pair.getSecond());
			}
			return disconnect;
		});
		// remove old listeners
		listeners.removeIf(listener -> {
			boolean disconnect = !newListeners.contains(listener);
			if (disconnect) {
				onDisconnect(listener);
				disconnectedNodes.add(listener);
			}
			return disconnect;
		});
	}

	private void connectNeighbours(Set<Node> visited, NodePath path, Set<Node> newListeners, Set<Pair<Node, Node>> newConnections, Node node) {
		var unvisitedNeighbours = node.getNeighbours().stream()
			.filter(neighbour -> !visited.contains(neighbour))
			.toList();
		visited.addAll(unvisitedNeighbours);
		unvisitedNeighbours.forEach(neighbour -> {
			if (!neighbour.wasRemoved() && isReceiver.test(neighbour)) {
				newListeners.add(neighbour);
				path.append(neighbour).forEach((from, to) -> newConnections.add(new Pair<>(from, to)));
			} else {
				connectNeighbours(visited, path.append(neighbour), newListeners, newConnections, neighbour);
			}
		});
	}

	public StreamState getState() {
		return state;
	}

	public void forListeners(Consumer<? super Node> consumer) {
		listeners.forEach(consumer);
	}

	public void forConnections(BiConsumer<Node, Node> consumer) {
		connections.forEach(pair -> consumer.accept(pair.getFirst(), pair.getSecond()));
	}

	private void connect(Node listener, NodePath path) {
		listeners.add(listener);
		path.forEach((from, to) -> connections.add(new Pair<>(from, to)));
		// Only execute consumer if not playing or is live
		if (state == StreamState.IDLE || isLive) connectConsumer.accept(listener);
	}

	private void onDisconnect(Node listener) {
		disconnectConsumer.accept(listener);
	}
}
