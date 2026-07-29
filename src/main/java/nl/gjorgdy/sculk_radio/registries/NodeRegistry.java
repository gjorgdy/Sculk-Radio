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
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.MicrophoneNode;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.RadioNode;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.SpeakerNode;
import nl.gjorgdy.sculk_radio.objects.nodes.redstone.RedstoneReceiverNode;
import nl.gjorgdy.sculk_radio.objects.nodes.redstone.RedstoneSourceNode;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static nl.gjorgdy.sculk_radio.utils.SetUtils.toList;

public class NodeRegistry extends SavedData {

	private static final Map<ServerLevel, NodeRegistry> registryMap = new HashMap<>();
	private final ServerLevel level;

	// audio
	private final Set<RadioNode> radioNodes = new HashSet<>();
	private final Set<SpeakerNode> speakerNodes = new HashSet<>();
	// microphone
	private final Set<MicrophoneNode> microphoneNodes = new HashSet<>();
	// redstone
	private final Set<RedstoneSourceNode> redstoneSourceNodes = new HashSet<>();
	private final Set<RedstoneReceiverNode> redstoneReceiverNodes = new HashSet<>();
	// communication
	private final Set<AntennaNode> antennaNodes = new HashSet<>();
	private final Set<RelayNode> relayNodes = new HashSet<>();

	private NodeRegistry(ServerLevel level, List<RadioNode> radios, List<SpeakerNode> speakers, List<RelayNode> relays, List<AntennaNode> antennas, List<MicrophoneNode> microphones, List<RedstoneReceiverNode> redstoneReceivers, List<RedstoneSourceNode> redstoneSources) {
		this(level);
		radios.forEach(this::registerInternal);
		speakers.forEach(this::registerInternal);
		relays.forEach(this::registerInternal);
		antennas.forEach(this::registerInternal);
		microphones.forEach(this::registerInternal);
		redstoneReceivers.forEach(this::registerInternal);
		redstoneSources.forEach(this::registerInternal);
	}

	private NodeRegistry(ServerLevel level) {
		this.level = level;
	}

	public void forSources(Consumer<SourceNode<?>> consumer) {
		radioNodes.forEach(consumer);
		microphoneNodes.forEach(consumer);
		redstoneSourceNodes.forEach(consumer);
	}

	public Set<MicrophoneNode> getMicrophonesInRange(BlockPos pos) {
		return microphoneNodes.stream()
			.filter(n -> n.getPos().distChessboard(pos) < SculkRadio.microphoneRange)
			.collect(Collectors.toSet());
	}

	public Set<AntennaNode> getAntennas(int frequency) {
		return antennaNodes.stream().filter(n -> n.getFrequency() == frequency).collect(Collectors.toSet());
	}

	public Optional<? extends Node> getNode(BlockPos pos) {
		Optional<? extends Node> node = radioNodes.stream().filter(n -> n.getPos().equals(pos)).findFirst();
		if (node.isPresent()) return node;
		node = speakerNodes.stream().filter(n -> n.getPos().equals(pos)).findFirst();
		if (node.isPresent()) return node;
		node = antennaNodes.stream().filter(n -> n.getPos().equals(pos)).findFirst();
		if (node.isPresent()) return node;
		node = microphoneNodes.stream().filter(n -> n.getPos().equals(pos)).findFirst();
		if (node.isPresent()) return node;
		node = relayNodes.stream().filter(n -> n.getPos().equals(pos)).findFirst();
		if (node.isPresent()) return node;
		node = redstoneReceiverNodes.stream().filter(n -> n.getPos().equals(pos)).findFirst();
		if (node.isPresent()) return node;
		node = redstoneSourceNodes.stream().filter(n -> n.getPos().equals(pos)).findFirst();
		return node;
	}

	public int size() {
		return radioNodes.size()
			+ speakerNodes.size()
			+ antennaNodes.size()
			+ relayNodes.size()
			+ microphoneNodes.size()
			+ redstoneReceiverNodes.size()
			+ redstoneSourceNodes.size();
	}

	private void forEach(Consumer<Node> nodeConsumer) {
		this.radioNodes.forEach(nodeConsumer);
		this.speakerNodes.forEach(nodeConsumer);
		this.antennaNodes.forEach(nodeConsumer);
		this.relayNodes.forEach(nodeConsumer);
		this.microphoneNodes.forEach(nodeConsumer);
		this.redstoneReceiverNodes.forEach(nodeConsumer);
		this.redstoneSourceNodes.forEach(nodeConsumer);
	}

	public <T extends Node> void register(@NonNull T node) {
		System.out.println("Registering node " + node.getClass().getSimpleName() + " at " + node.getPos());
		registerInternal(node);
		setDirty();
		System.out.println(" with " + node.getNeighbours().size() + " neighbours");
	}

	private <T extends Node> void registerInternal(@NonNull T node) {
		forEach(otherNode -> {
			if (node.canConnect(otherNode)) {
				node.connect(otherNode);
			}
		});
		switch (node) {
			case RadioNode radioNode -> radioNodes.add(radioNode);
			case SpeakerNode speakerNode -> speakerNodes.add(speakerNode);
			case AntennaNode antennaNode -> antennaNodes.add(antennaNode);
			case RelayNode relayNode -> relayNodes.add(relayNode);
			case MicrophoneNode microphoneNode -> microphoneNodes.add(microphoneNode);
			case RedstoneReceiverNode redstoneReceiverNode -> redstoneReceiverNodes.add(redstoneReceiverNode);
			case RedstoneSourceNode redstoneSourceNode -> redstoneSourceNodes.add(redstoneSourceNode);
			default -> {}
		}
		node.init(this.level);
	}

	public void remove(@NonNull Node node) {
		switch (node) {
			case RadioNode radioNode -> radioNodes.remove(radioNode);
			case SpeakerNode speakerNode -> speakerNodes.remove(speakerNode);
			case AntennaNode antennaNode -> antennaNodes.remove(antennaNode);
			case RelayNode relayNode -> relayNodes.remove(relayNode);
			case MicrophoneNode microphoneNode -> microphoneNodes.remove(microphoneNode);
			case RedstoneReceiverNode redstoneReceiverNode -> redstoneReceiverNodes.remove(redstoneReceiverNode);
			case RedstoneSourceNode redstoneSourceNode -> redstoneSourceNodes.remove(redstoneSourceNode);
			default -> {}
		}
		node.afterRemove();
		setDirty();
	}

	public static NodeRegistry of(ServerLevel level) {
		return registryMap.computeIfAbsent(level, NodeRegistry::load);
	}

	private static NodeRegistry load(ServerLevel level) {
		Codec<NodeRegistry> codec = RecordCodecBuilder.create(instance -> instance.group(
               RadioNode.CODEC.listOf().fieldOf("radios").forGetter(i -> toList(i.radioNodes)),
               SpeakerNode.CODEC.listOf().fieldOf("speakers").forGetter(i -> toList(i.speakerNodes)),
               RelayNode.CODEC.listOf().fieldOf("relays").forGetter(i -> toList(i.relayNodes)),
               AntennaNode.CODEC.listOf().fieldOf("antennas").forGetter(i -> toList(i.antennaNodes)),
               MicrophoneNode.CODEC.listOf().fieldOf("microphones").forGetter(i -> toList(i.microphoneNodes)),
               RedstoneReceiverNode.CODEC.listOf().fieldOf("redstone_receivers").forGetter(i -> toList(i.redstoneReceiverNodes)),
               RedstoneSourceNode.CODEC.listOf().fieldOf("redstone_sources").forGetter(i -> toList(i.redstoneSourceNodes))
	       ).apply(instance, (ra, sp, re, an, mi, rr, rs) -> new NodeRegistry(level, ra, sp, re, an, mi, rr, rs))
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
