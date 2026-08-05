package nl.gjorgdy.sculk_radio.registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import nl.gjorgdy.sculk_radio.objects.nodes.redstone.RedstoneTransmitterNode;
import nl.gjorgdy.sculk_radio.objects.nodes.teleport.TeleportReceiverNode;
import nl.gjorgdy.sculk_radio.objects.nodes.teleport.TeleportTransmitterNode;
import nl.gjorgdy.sculk_radio.objects.streams.StreamState;
import nl.gjorgdy.sculk_radio.utils.VisualUtils;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NodeRegistry extends SavedData {

	private static final Map<ServerLevel, NodeRegistry> registryMap = new HashMap<>();
	private final ServerLevel level;

	// audio
	private final TypedSourceNodeRegistry<RadioNode> radioNodes = new TypedSourceNodeRegistry<>(n -> n instanceof RadioNode);
	private final TypedNodeRegistry<SpeakerNode> speakerNodes = new TypedNodeRegistry<>(n -> n instanceof SpeakerNode);
	// microphone
	private final TypedSourceNodeRegistry<MicrophoneNode> microphoneNodes = new TypedSourceNodeRegistry<>(
			n -> n instanceof MicrophoneNode, SculkRadio::microphonesEnabled);
	// redstone
	private final TypedSourceNodeRegistry<RedstoneTransmitterNode> redstoneSourceNodes = new TypedSourceNodeRegistry<>(
			n -> n instanceof RedstoneTransmitterNode, () -> SculkRadio.redstoneEnabled);
	private final TypedNodeRegistry<RedstoneReceiverNode> redstoneReceiverNodes = new TypedNodeRegistry<>(
			n -> n instanceof RedstoneReceiverNode, () -> SculkRadio.redstoneEnabled);
	// teleportation
	private final TypedSourceNodeRegistry<TeleportTransmitterNode> teleportTransmitterNodes = new TypedSourceNodeRegistry<>(
			n -> n instanceof TeleportTransmitterNode, () -> SculkRadio.teleportEnabled);
	private final TypedNodeRegistry<TeleportReceiverNode> teleportReceiverNodes = new TypedNodeRegistry<>(
			n -> n instanceof TeleportReceiverNode, () -> SculkRadio.teleportEnabled);
	// communication
	private final TypedNodeRegistry<AntennaNode> antennaNodes = new TypedNodeRegistry<>(
			n -> n instanceof AntennaNode, () -> SculkRadio.antennasEnabled);
	private final TypedNodeRegistry<RelayNode> relayNodes = new TypedNodeRegistry<>(n -> n instanceof RelayNode);

	private final TypedNodeRegistry<?>[] nodeRegistries = new TypedNodeRegistry<?>[] {
		radioNodes,
		speakerNodes,
		microphoneNodes,
		redstoneSourceNodes,
		redstoneReceiverNodes,
		teleportTransmitterNodes,
		teleportReceiverNodes,
		antennaNodes,
		relayNodes
	};

	private final TypedSourceNodeRegistry<?>[] sourceNodeRegistries = new TypedSourceNodeRegistry[] {
		radioNodes,
		microphoneNodes,
		redstoneSourceNodes,
		teleportTransmitterNodes
	};

	@SafeVarargs
	private NodeRegistry(ServerLevel level, List<? extends Node>... lists) {
		this(level);
		for (var list : lists) {
			if (list == null) return;
			list.forEach(this::registerInternal);
		}
		setDirty();
	}

	private NodeRegistry(ServerLevel level) {
		this.level = level;
	}

	public void forSources(Consumer<SourceNode<?>> consumer) {
		for (var reg : sourceNodeRegistries) {
			reg.forEachNode(consumer);
		}
	}

	public Set<MicrophoneNode> getActiveMicrophonesInRange(BlockPos pos) {
		return microphoneNodes.filter(n ->
	          n.getState() == StreamState.ACTIVE
				&& n.getPos().distChessboard(pos) < SculkRadio.microphoneRange
		);
	}

	public Set<AntennaNode> getAntennas(int frequency) {
		return antennaNodes.filter(n -> n.getFrequency() == frequency);
	}

	public Optional<? extends Node> getNode(BlockPos pos) {
		for (var reg : nodeRegistries) {
			var node = reg.get(pos);
			if (node.isPresent()) return node;
		}
		return Optional.empty();
	}

	public int size() {
		int size = 0;
		for (var reg : nodeRegistries) {
			size += reg.size();
		}
		return size;
	}

	private void forEach(Consumer<Node> nodeConsumer) {
		for (var reg : nodeRegistries) {
			reg.forEachNode(nodeConsumer);
		}
	}

	public <T extends Node> boolean register(@NonNull T node) {
		if (registerInternal(node)) {
			setDirty();
			level.playSound(null, node.getPos(), SoundEvents.SCULK_CLICKING, SoundSource.BLOCKS, 2f, 2f);
			node.pulseNeighbours();
			return true;
		}
		return false;
	}

	private <T extends Node> boolean registerInternal(@NonNull T node) {
		for (var reg : nodeRegistries) {
			// Find repo for this node type
			if (reg.typePredicate.test(node)) {
				if (reg.disabled()) return false;
				// Connect node to neighbours
				forEach(otherNode -> {
					if (canConnect(node, otherNode)) {
						node.connect(otherNode);
					}
				});
				reg.add(node);
				node.init(this.level);
				return true;
			}
		}
		return false;
	}

	private boolean canConnect(Node nodeA, Node nodeB) {
		return nodeA.canConnect(nodeB) && nodeB.canConnect(nodeA);
	}

	public void remove(@NonNull Node node) {
		for (var reg : nodeRegistries) {
			if (reg.typePredicate.test(node)) {
				reg.remove(node);
				break;
			}
		}
		node.afterRemove();
		setDirty();
	}

	public static NodeRegistry of(ServerLevel level) {
		return registryMap.computeIfAbsent(level, NodeRegistry::load);
	}

	private static NodeRegistry load(ServerLevel level) {
		Codec<NodeRegistry> codec = RecordCodecBuilder.create(instance -> instance.group(
				RadioNode.CODEC.listOf().optionalFieldOf("radios").forGetter(i -> i.radioNodes.toList()),
				SpeakerNode.CODEC.listOf().optionalFieldOf("speakers").forGetter(i -> i.speakerNodes.toList()),
				RelayNode.CODEC.listOf().optionalFieldOf("relays").forGetter(i -> i.relayNodes.toList()),
				AntennaNode.CODEC.listOf().optionalFieldOf("antennas").forGetter(i -> i.antennaNodes.toList()),
				MicrophoneNode.CODEC.listOf().optionalFieldOf("microphones").forGetter(i -> i.microphoneNodes.toList()),
				RedstoneTransmitterNode.CODEC.listOf().optionalFieldOf("redstone_transmitters").forGetter(i -> i.redstoneSourceNodes.toList()),
				RedstoneReceiverNode.CODEC.listOf().optionalFieldOf("redstone_receivers").forGetter(i -> i.redstoneReceiverNodes.toList()),
				TeleportTransmitterNode.CODEC.listOf().optionalFieldOf("teleport_transmitters").forGetter(i -> i.teleportTransmitterNodes.toList()),
				TeleportReceiverNode.CODEC.listOf().optionalFieldOf("teleport_receivers").forGetter(i -> i.teleportReceiverNodes.toList())
	       ).apply(instance, (a, b, c, d, e, f, g, h, i)
				-> new NodeRegistry(level, a.orElse(null), b.orElse(null), c.orElse(null), d.orElse(null), e.orElse(null), f.orElse(null), g.orElse(null), h.orElse(null), i.orElse(null)))
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

	private class TypedSourceNodeRegistry<T extends SourceNode<?>> extends TypedNodeRegistry<T> {

		private TypedSourceNodeRegistry(Predicate<Node> typePredicate) {
			super(typePredicate);
		}

		private TypedSourceNodeRegistry(Predicate<Node> typePredicate, Supplier<Boolean> enabledSupplier) {
			super(typePredicate, enabledSupplier);
		}

		private void forEachNode(Consumer<SourceNode<?>> consumer) {
			if (!enabled.get()) return;
			nodes.forEach(consumer);
		}

	}

	private static class TypedNodeRegistry<T extends Node> {

		public final Predicate<Node> typePredicate;
		protected final Supplier<Boolean> enabled;
		protected final Set<T> nodes = new HashSet<>();

		private TypedNodeRegistry(Predicate<Node> typePredicate) {
			this(typePredicate, () -> true);
		}

		private TypedNodeRegistry(Predicate<Node> typePredicate, Supplier<Boolean> enabled) {
			this.typePredicate = typePredicate;
			this.enabled = enabled;
		}

		public boolean disabled() {
			return !enabled.get();
		}

		Optional<List<T>> toList() {
			if (disabled() || nodes.isEmpty()) return Optional.empty();
			return Optional.of(new ArrayList<>(nodes));
		}

		private void add(Node node) {
			//noinspection unchecked
			nodes.add((T) node);
		}

		private void remove(Node node) {
			//noinspection unchecked
			nodes.remove((T) node);
		}

		Set<T> filter(Predicate<T> filter) {
			return nodes.stream().filter(filter).collect(Collectors.toSet());
		}

		private void forEachNode(Consumer<Node> consumer) {
			nodes.forEach(consumer);
		}

		private Optional<T> get(BlockPos pos) {
			return nodes.stream().filter(node -> node.getPos().equals(pos)).findFirst();
		}

		public int size() {
			return nodes.size();
		}

	}

}
