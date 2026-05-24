package nl.gjorgdy.sculk_radio.connections;

import kotlin.Pair;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.interfaces.IStreamTransmitter;
import nl.gjorgdy.sculk_radio.nodes.Node;
import nl.gjorgdy.sculk_radio.nodes.RelayNode;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class SculkCluster {

	private final ServerLevel level;
	private final Set<ClusterNode> nodes;
	private final HashMap<Pair<Node, Node>, ArrayDeque<Node>> pathsCache;

	public SculkCluster(ServerLevel level) {
		this.level = level;
		this.nodes = new HashSet<>();
		// clear cache
		this.pathsCache = new HashMap<>();
	}

	public void announceStream(IStreamTransmitter sourceNode) {
		var stream = sourceNode.getStream();
		if (stream == null) return;
		nodes.forEach(node -> {
			if (node != sourceNode && node instanceof Node n) {
				if (n.canReceive()) stream.listen(n);
			}
		});
	}

	public Set<SculkStream> getStreams() {
		return nodes.stream().map(node -> {
			if (node instanceof IStreamTransmitter transmitter) {
				return transmitter.getStream();
			}
			return null;
		})
		.filter(Objects::nonNull)
		.collect(Collectors.toSet());
	}

	public ServerLevel getLevel() {
		return level;
	}

	public SculkCluster merge(SculkCluster other) {
		return merge(this, other);
	}

	public static SculkCluster merge(SculkCluster a, SculkCluster b) {
		if (!a.getLevel().equals(b.getLevel())) {
			throw new IllegalArgumentException("Cannot merge subnetworks from different dimensions");
		}
		if (a.nodes.size() < b.nodes.size()) {
			return merge(b, a);
		}
		for (var node : b.nodes) {
			a.joinCluster(node);
		}
		b.nodes.clear();
		// clear caches
		a.pathsCache.clear();
		b.pathsCache.clear();
		return a;
	}

	public void joinCluster(ClusterNode node) {
		node.cluster = this;
		nodes.add(node);
		// connect to streams
		var streams = getStreams();
		nodes.forEach(n -> streams.forEach(s -> s.listen(n)));
		// clear cache
		pathsCache.clear();
	}

	public void leaveCluster(ClusterNode node) {
		node.cluster = null;
		nodes.remove(node);
		// leave streams
		var streams = getStreams();
		nodes.forEach(n -> streams.forEach(s -> s.stopListening(n)));
		// clear cache
		pathsCache.clear();
	}

	public void splitCluster() {
		var oldNodes = nodes.stream().filter(n -> !n.isRemoved).toList();
		if (oldNodes.isEmpty()) return;
		// Find connected components using BFS
		var visited = new HashSet<ClusterNode>();
		var components = new ArrayList<Set<ClusterNode>>();
		for (var node : oldNodes) {
			if (!visited.contains(node)) {
				var component = findConnectedComponent(node, oldNodes, visited);
				components.add(component);
			}
		}
		// If only one component, no split needed
		if (components.size() == 1) return;
		// Keep first component in current cluster, create new clusters for others
		var componentIter = components.iterator();
		var mainComponent = componentIter.next();
		// Remove nodes that are not in the main component
		var disconnected = oldNodes.stream()
			.filter(n -> !mainComponent.contains(n))
			.collect(Collectors.toCollection(HashSet::new));
		disconnected.forEach(this::leaveCluster);
		// Assign other components to new clusters
		while (componentIter.hasNext()) {
			var component = componentIter.next();
			var newCluster = new SculkCluster(level);
			for (var node : component) {
				newCluster.joinCluster(node);
			}
		}
		// clear cache
		pathsCache.clear();
	}

	private Set<ClusterNode> findConnectedComponent(ClusterNode start, java.util.List<ClusterNode> allNodes, Set<ClusterNode> visited) {
		var component = new HashSet<ClusterNode>();
		var queue = new ArrayDeque<ClusterNode>();
		queue.add(start);
		visited.add(start);
		component.add(start);

		while (!queue.isEmpty()) {
			var current = queue.removeFirst();
			for (var node : allNodes) {
				if (!visited.contains(node) && current.canConnect(node)) {
					visited.add(node);
					component.add(node);
					queue.addLast(node);
				}
			}
		}

		return component;
	}

	public void path(BiConsumer<Node, Node> edgeConsumer, Node from, Node to) {
		if (from == null || to == null || from.equals(to)) {
			return;
		}
		if (!nodes.contains(from) || !nodes.contains(to)) {
			return;
		}
		if (from.canConnect(to, false)) {
			edgeConsumer.accept(from, to);
			return;
		}
		// Try to get path from cache
		var path = pathsCache.getOrDefault(new Pair<>(from, to), null);
		if (path == null) {
			path = pathfind(from, to);
			pathsCache.put(new Pair<>(from, to), path);
		}
		if (path == null) return;

		var iterator = path.iterator();
		var previousNode = iterator.next();
		while (iterator.hasNext()) {
			var nextNode = iterator.next();
			edgeConsumer.accept(previousNode, nextNode);
			previousNode = nextNode;
		}
	}

	private ArrayDeque<Node> pathfind(Node from, Node to) {
		var queue = new ArrayDeque<Node>();
		var previous = new HashMap<Node, Node>();
		queue.add(from);
		previous.put(from, null);
		while (!queue.isEmpty() && !previous.containsKey(to)) {
			var current = queue.removeFirst();
			for (var clusterNode : nodes) {
				if (!(clusterNode instanceof Node next)) {
					continue;
				}
				if (previous.containsKey(next) || next == current) {
					continue;
				}
				if (!(next instanceof RelayNode) && next != to) {
					continue;
				}
				if (current.canConnect(next, false)) {
					previous.put(next, current);
					queue.addLast(next);
				}
			}
		}
		if (!previous.containsKey(to)) {
			return null;
		}
		var path = new ArrayDeque<Node>();
		for (var current = to; current != null; current = previous.get(current)) {
			path.addFirst(current);
		}
		return path;
	}

	public int size() {
		return nodes.size();
	}

	@Override
	public String toString() {
		return "Cluster[" +  String.join(",", nodes.stream().map(Objects::toString).toList()) + "]";
	}

	public abstract static class ClusterNode {

		protected boolean isRemoved = false;
		private SculkCluster cluster;

		public ClusterNode(ServerLevel level) {
			var cluster = new SculkCluster(level);
			cluster.joinCluster(this);
		}

		public SculkCluster getCluster() {
			return cluster;
		}

		private void setCluster(SculkCluster cluster) {
			this.cluster = cluster;
		}

		public void onRemove() {
			// leave cluster
			this.cluster.splitCluster();
			// set removed
			isRemoved = true;
		}

		abstract public boolean canTransmit();
		abstract public boolean canReceive();

		protected boolean innerCanConnect(ClusterNode otherNode) {
			var thisToOther = this.canTransmit() && otherNode.canReceive();
			var otherToThis = this.canReceive() && otherNode.canTransmit();
			return (thisToOther || otherToThis);// && !alreadyConnected;
		}

		final public boolean canConnect(ClusterNode otherNode) {
			return canConnect(otherNode, true);
		}

		final public boolean canConnect(ClusterNode otherNode, boolean checkIfAlreadyConnected) {
			var alreadyConnected = this.cluster == otherNode.cluster;
			return (innerCanConnect(otherNode) && otherNode.innerCanConnect(this))
					&& (!checkIfAlreadyConnected || !alreadyConnected);
		}
	}

}
