package nl.gjorgdy.sculk_radio.objects.streams;

import kotlin.Pair;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.objects.nodes.AntennaNode;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.RelayNode;
import nl.gjorgdy.sculk_radio.objects.NodePath;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.ReceiverNode;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;
import nl.gjorgdy.sculk_radio.utils.VisualUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Stream {

	private final Predicate<? super Node> isReceiver;
	private final Consumer<? super Node> connectConsumer;
	private final Consumer<? super Node> disconnectConsumer;
	// If nodes can 'connect' after playing has started
	private final boolean isLive;
	protected final SourceNode source;

	private final Set<Pair<Node, Node>> connections = new HashSet<>();
	private final Set<ReceiverNode> listeners;
	private final Queue<Node> disconnectedNodes;
	private StreamState state = StreamState.IDLE;

	public Stream(
		Predicate<? super Node> isReceiver,
		Consumer<? super Node> connectConsumer,
		Consumer<? super Node> disconnectConsumer,
		SourceNode source,
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
		connectionTick();
		visualsTick(source.getLevel());
		redstoneTick();
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
		// to reset visual state
		visualsTick(source.getLevel());
		redstoneTick();
	}

	public void visualsTick(ServerLevel level) {
		if (state == StreamState.ACTIVE) {
			VisualUtils.spawnShriekerParticles(level, source.getPos());
			forListeners(Node::visualsTick);
			forConnections((from, to) -> {
				// Between antennas
				if (from instanceof AntennaNode && to instanceof AntennaNode) {
					if (from.isLoaded()) {
						from.visualsTick();
						VisualUtils.spawnVibrationParticles(level, from.getPos(), from.getPos().above(16));
						VisualUtils.spawnAntennaParticles(level, from.getPos());
					}
					if (to.isLoaded()) {
						to.visualsTick();
						VisualUtils.spawnVibrationParticles(level, to.getPos().above(16), to.getPos());
						VisualUtils.spawnAntennaParticles(level, to.getPos());
					}
					return;
				}
				// Between relays and/or an antenna
				else if (from instanceof RelayNode && from.isLoaded()) {
					if (to instanceof AntennaNode) {
						VisualUtils.spawnAntennaParticles(level, from.getPos());
					}
					from.visualsTick();
				}
				// Between anything else
				if (from.isLoaded() && to.isLoaded()) {
					if (from instanceof AntennaNode) {
						VisualUtils.spawnAntennaParticles(level, to.getPos());
					}
					VisualUtils.spawnVibrationParticles(level, from.getPos(), to.getPos());
				}
			});
		}
		while (!disconnectedNodes.isEmpty()) {
			Node node = disconnectedNodes.poll();
			if (node == null) continue;
			VisualUtils.deactivateSensor(level, node.getPos());
		}
	}

	public void redstoneTick() {
		int redstone = source.getRedstoneSignal();
		listeners.forEach(listener -> listener.setRedstoneSignal(redstone));
		int analogRedstone = source.getAnalogRedstoneSignal();
		listeners.forEach(listener -> listener.setAnalogRedstoneSignal(analogRedstone));
	}

	public void connectionTick() {
		if (state == StreamState.STOPPED) return;
		Set<ReceiverNode> newListeners = new HashSet<>();
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
		redstoneTick();
	}

	private void connectNeighbours(Set<Node> visited, NodePath path, Set<ReceiverNode> newListeners, Set<Pair<Node, Node>> newConnections, Node node) {
		var unvisitedNeighbours = node.getNeighbours().stream()
			.filter(neighbour -> !visited.contains(neighbour))
			.toList();
		visited.addAll(unvisitedNeighbours);
		unvisitedNeighbours.forEach(neighbour -> {
			if (!neighbour.wasRemoved() && isReceiver.test(neighbour) && neighbour instanceof ReceiverNode receiver) {
				newListeners.add(receiver);
				path.append(neighbour).forEach((from, to) -> newConnections.add(new Pair<>(from, to)));
			} else {
				connectNeighbours(visited, path.append(neighbour), newListeners, newConnections, neighbour);
			}
		});
	}

	public StreamState getState() {
		return state;
	}

	public void forListeners(Consumer<? super ReceiverNode> consumer) {
		listeners.forEach(consumer);
	}

	public void forConnections(BiConsumer<Node, Node> consumer) {
		connections.forEach(pair -> consumer.accept(pair.getFirst(), pair.getSecond()));
	}

	private void connect(ReceiverNode listener, NodePath path) {
		listeners.add(listener);
		path.forEach((from, to) -> connections.add(new Pair<>(from, to)));
		// Only execute consumer if not playing or is live
		if (state == StreamState.IDLE || isLive) connectConsumer.accept(listener);
	}

	private void onDisconnect(ReceiverNode listener) {
		disconnectConsumer.accept(listener);
		listener.setAnalogRedstoneSignal(0);
		listener.setRedstoneSignal(0);
	}
}
