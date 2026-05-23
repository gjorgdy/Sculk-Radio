package nl.gjorgdy.sculk_radio.connections;

import kotlin.Pair;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.nodes.Node;
import nl.gjorgdy.sculk_radio.nodes.RelayNode;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

	public void forNodes(Consumer<? super ClusterNode> action) {
		this.nodes.forEach(action);
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
			a.nodes.add(node);
			node.setCluster(a);
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
		// clear cache
		pathsCache.clear();
	}

	public void splitCluster() {
		var oldNodes = nodes.stream().filter(n -> !n.isRemoved).toList();
		// wipe cluster and nodes
		nodes.clear();
		oldNodes.forEach(node -> node.setCluster(null));
		// rebuild cluster(s)
		for (var node : oldNodes) {
			// give cluster if none
			if (node.cluster == null) {
				var cluster = nodes.isEmpty() ? this : new SculkCluster(level);
				cluster.joinCluster(node);
			}
			// try to connect
			for (var otherNode : oldNodes) {
				if (node != otherNode && node.canConnect(otherNode)) {
					if (otherNode.cluster == null) {
						node.getCluster().joinCluster(otherNode);
					} else {
						node.getCluster().merge(otherNode.getCluster());
					}
				}
			}
		}
		// clear cache
		pathsCache.clear();
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
			this.cluster.nodes.remove(this);
			this.cluster.splitCluster();
			this.cluster = null;
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
