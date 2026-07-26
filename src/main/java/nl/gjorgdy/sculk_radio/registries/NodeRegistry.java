package nl.gjorgdy.sculk_radio.registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.objects.nodes.AntennaNode;
import nl.gjorgdy.sculk_radio.objects.nodes.RelayNode;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.RadioNode;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.SpeakerNode;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Consumer;

import static nl.gjorgdy.sculk_radio.utils.SetUtils.toList;

public class NodeRegistry extends SavedData {

	// audio
	private final Set<RadioNode> radioNodes = new HashSet<>();
	private final Set<SpeakerNode> speakerNodes = new HashSet<>();
	// communication
	private final Set<AntennaNode> antennaNodes = new HashSet<>();
	private final Set<RelayNode> relayNodes = new HashSet<>();

	private NodeRegistry(ServerLevel level, List<RadioNode> radios, List<SpeakerNode> speakers, List<RelayNode> relays, List<AntennaNode> antennas) {
		this(level);
		radios.forEach(this::registerInternal);
		speakers.forEach(this::registerInternal);
		relays.forEach(this::registerInternal);
		antennas.forEach(this::registerInternal);
	}

	private NodeRegistry(ServerLevel level) {}

	public Optional<? extends Node> getNode(BlockPos pos) {
		Optional<? extends Node> node = radioNodes.stream().filter(n -> n.getPos().equals(pos)).findFirst();
		if (node.isPresent()) return node;
		node = speakerNodes.stream().filter(n -> n.getPos().equals(pos)).findFirst();
		if (node.isPresent()) return node;
		node = antennaNodes.stream().filter(n -> n.getPos().equals(pos)).findFirst();
		if (node.isPresent()) return node;
		return relayNodes.stream().filter(n -> n.getPos().equals(pos)).findFirst();
	}

	public int size() {
		return radioNodes.size() + speakerNodes.size() + antennaNodes.size() + relayNodes.size();
	}

	private void forEach(Consumer<Node> nodeConsumer) {
		this.radioNodes.forEach(nodeConsumer);
		this.speakerNodes.forEach(nodeConsumer);
		this.antennaNodes.forEach(nodeConsumer);
		this.relayNodes.forEach(nodeConsumer);
	}

	public <T extends Node> void register(@NonNull T node) {
		registerInternal(node);
		setDirty();
	}

	private <T extends Node> void registerInternal(@NonNull T node) {
		forEach(otherNode -> {
			if (node.canConnect(otherNode)) {
				node.connect(otherNode);
			}
		});
		System.out.println("Registering node: " + node.getClass().getSimpleName() + " at " + node.getPos() + " with " + node.getNeighbours().size() + " neighbours");
		switch (node) {
			case RadioNode radioNode -> radioNodes.add(radioNode);
			case SpeakerNode speakerNode -> speakerNodes.add(speakerNode);
			case RelayNode relayNode -> relayNodes.add(relayNode);
			case AntennaNode antennaNode -> antennaNodes.add(antennaNode);
			default -> {}
		}
	}

	public void remove(@NonNull Node node) {
		switch (node) {
			case RadioNode radioNode -> radioNodes.remove(radioNode);
			case SpeakerNode speakerNode -> speakerNodes.remove(speakerNode);
			case RelayNode relayNode -> relayNodes.remove(relayNode);
			case AntennaNode antennaNode -> antennaNodes.remove(antennaNode);
			default -> {}
		}
		node.afterRemove();
		setDirty();
	}

	public static NodeRegistry of(ServerLevel level) {
		Codec<NodeRegistry> codec = RecordCodecBuilder.create(instance -> instance.group(
               RadioNode.CODEC.listOf().fieldOf("radios").forGetter(i -> toList(i.radioNodes)),
               SpeakerNode.CODEC.listOf().fieldOf("speakers").forGetter(i -> toList(i.speakerNodes)),
               RelayNode.CODEC.listOf().fieldOf("relays").forGetter(i -> toList(i.relayNodes)),
               AntennaNode.CODEC.listOf().fieldOf("antennas").forGetter(i -> toList(i.antennaNodes))
	       ).apply(instance, (ra, sp, re, an) -> new NodeRegistry(level, ra, sp, re, an))
		);
		//noinspection DataFlowIssue
		var type = new SavedDataType<>(
			Identifier.fromNamespaceAndPath(SculkRadio.MOD_ID, "nodes"),
			() -> new NodeRegistry(level),
			codec,
			null
		);
		return level.getDataStorage().computeIfAbsent(type);
	}

}
