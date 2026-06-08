package nl.gjorgdy.sculk_radio.connections;

import kotlin.Pair;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.SculkRadio;
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
			if (node != sourceNode && node instanceof SculkStream.ListeningNode l) {
				l.listenTo(stream);
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
			throw new IllegalArgumentException("Cannot merge clusters from different dimensions");
		}
		// merge into biggest
		if (a.nodes.size() < b.nodes.size()) {
			return merge(b, a);
		}

		if (b.nodes.size() == 1) {
			a.joinCluster(b.nodes.iterator().next());
		} else {
			for (var node : b.nodes) {
				a.joinCluster(node);
			}
		}

		b.nodes.clear();
		a.pathsCache.clear();
		return a;
	}

	public void joinCluster(ClusterNode node) {
		node.cluster = this;
		nodes.add(node);
		// connect to streams
		if (node instanceof SculkStream.ListeningNode l) {
			l.stopListening();
			getStreams().forEach(l::listenTo);
		}
		// clear cache
		pathsCache.clear();
	}

	public void leaveCluster(ClusterNode node) {
		leaveCluster(node, true);
	}

	public void leaveCluster(ClusterNode node, boolean updateCluster) {
		if (node instanceof SculkStream.ListeningNode l) l.stopListening();
		node.cluster = null;
		nodes.remove(node);
		// handle removal in cluster
		if (!updateCluster) return;
		var neighbors = nodes.stream().filter(n -> n.canConnect(node)).toList();
		if (node.isEndpoint() || neighbors.size() == 1) {
			// remove from cache
			pathsCache.keySet().removeIf(pair -> pair.getFirst() == node || pair.getSecond() == node);
		} else {
			// check connections of neighbors
			neighbors.stream()
				.filter(neighbor -> nodes.stream().noneMatch(neighbor::canConnect))
				.forEach(this::orphan);
			// reset whole cache
			pathsCache.clear();
		}
	}

	private void orphan(ClusterNode node) {
		System.out.println("Node " + node + " orphaned after removing " + node);
		if (node instanceof SculkStream.ListeningNode l) l.stopListening();
		nodes.remove(node);
		new SculkCluster(level).joinCluster(node);
		SculkRadio.getLevelRegistry()
				.getNodeRegistry(level)
				.initNode((Node) node);
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

	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj instanceof SculkCluster cluster && cluster.nodes.equals(nodes));
	}

	@Override
	public int hashCode() {
		return nodes.hashCode();
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

		public void afterRemove() {
			// leave cluster
			this.cluster.leaveCluster(this);
			// set removed
			isRemoved = true;
		}

		abstract public boolean canTransmit();
		abstract public boolean canReceive();

		public boolean isEndpoint() {
			return canTransmit() != canReceive();
		}

		protected boolean innerCanConnect(ClusterNode otherNode) {
			if (this == otherNode) return false;
			var thisToOther = this.canTransmit() && otherNode.canReceive();
			var otherToThis = this.canReceive() && otherNode.canTransmit();
			return (thisToOther || otherToThis);
		}

		final public boolean canConnect(ClusterNode otherNode) {
			return (innerCanConnect(otherNode) && otherNode.innerCanConnect(this));
		}

		final public boolean canConnect(ClusterNode otherNode, boolean checkIfAlreadyConnected) {
			var alreadyConnected = this.cluster == otherNode.cluster;
			return (innerCanConnect(otherNode) && otherNode.innerCanConnect(this))
					&& (!checkIfAlreadyConnected || !alreadyConnected);
		}
	}

}
