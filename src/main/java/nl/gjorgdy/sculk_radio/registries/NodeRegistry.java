package nl.gjorgdy.sculk_radio.registries;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.nodes.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class NodeRegistry {

	private final ServerLevel level;
	private final Set<Node> nodes;

	private final Set<AntennaNode> antennas;
	private final Set<RadioNode> radios;
	private final Set<RelayNode> relays;
	private final Set<SpeakerNode> speakers;

	public NodeRegistry(ServerLevel level) {
		this.level = level;
		this.nodes = new HashSet<>();
		this.antennas = new HashSet<>();
		this.radios = new HashSet<>();
		this.relays = new HashSet<>();
		this.speakers = new HashSet<>();
	}

	public Optional<Node> getNode(BlockPos pos) {
		return nodes.stream().filter(n -> n.getPos().equals(pos)).findAny();
	}

	public AntennaNode registerAntenna(BlockPos pos) {
		if (pos == null) throw new IllegalStateException("Cannot register a node at a null position");
		var antenna = new AntennaNode(level, pos);
		initNode(antenna);
		return antenna;
	}

	public RadioNode registerRadio(BlockPos pos) {
		if (pos == null) throw new IllegalStateException("Cannot register a node at a null position");
		var radio = new RadioNode(level, pos);
		initNode(radio);
		return radio;
	}

	public RelayNode registerRelay(BlockPos pos) {
		if (pos == null) throw new IllegalStateException("Cannot register a node at a null position");
		var relay = new RelayNode(level, pos);
		initNode(relay);
		return relay;
	}

	public SpeakerNode registerSpeaker(BlockPos pos) {
		if (pos == null) throw new IllegalStateException("Cannot register a node at a null position");
		var speaker = new SpeakerNode(level, pos);
		initNode(speaker);
		return speaker;
	}

	public void initNode(Node node) {
		// add to registry
		switch (node) {
			case AntennaNode antenna -> antennas.add(antenna);
			case RadioNode radio -> radios.add(radio);
			case RelayNode relay -> relays.add(relay);
			case SpeakerNode speaker -> speakers.add(speaker);
			default -> {}
		}
		nodes.add(node);
		// connect to neighboring clusters
		var neighboringClusters = nodes.stream()
				.filter(node::canConnect)
				.map(Node::getCluster)
				.collect(Collectors.toSet());
		for (var c : neighboringClusters) {
			node.getCluster().merge(c);
		}
		System.out.println("Registered new node " + node + " at " + node.getPos() + " in cluster of " + node.getCluster().size());
	}

	public void removeNode(BlockPos pos) {
		getNode(pos).ifPresent(this::removeNode);
	}

	public void removeNode(Node node) {
		if (node == null) return;
		// remove from registry
		nodes.remove(node);
		switch (node) {
			case AntennaNode antenna -> antennas.remove(antenna);
			case RadioNode radio -> radios.remove(radio);
			case RelayNode relay -> relays.remove(relay);
			case SpeakerNode speaker -> speakers.remove(speaker);
			default -> {}
		}
		// handle removal
		node.afterRemove();
	}

}
