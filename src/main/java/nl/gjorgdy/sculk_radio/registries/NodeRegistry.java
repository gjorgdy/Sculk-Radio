package nl.gjorgdy.sculk_radio.registries;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.nodes.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class NodeRegistry {

	private final ServerLevel level;
	private final Set<Node> nodes;

	public NodeRegistry(ServerLevel level) {
		this.level = level;
		this.nodes = new HashSet<>();
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
		nodes.add(node);
		// connect to neighboring clusters
		nodes.stream()
				.filter(node::canConnect)
				.forEach(node::connect);
	}

	public void removeNode(Node node) {
		if (node == null) return;
		// remove from registry
		nodes.remove(node);
		// handle removal
		node.afterRemove();
	}

}
