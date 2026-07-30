package nl.gjorgdy.sculk_radio.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.nodes.AntennaNode;
import nl.gjorgdy.sculk_radio.objects.nodes.RelayNode;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.MicrophoneNode;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.RadioNode;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.SpeakerNode;
import nl.gjorgdy.sculk_radio.objects.nodes.redstone.RedstoneReceiverNode;
import nl.gjorgdy.sculk_radio.objects.nodes.redstone.RedstoneSourceNode;
import nl.gjorgdy.sculk_radio.registries.NodeRegistry;
import org.jspecify.annotations.Nullable;

public abstract class NodeUtils {

	public static void register(ServerLevel level, BlockState state, BlockEntity blockEntity) {
		// check if exists
		var registry = NodeRegistry.of(level);
		var node = registry.getNode(blockEntity.getBlockPos()).orElse(null);
		if (node != null) {
			if (blockEntity instanceof INodeContainer nodeContainer) {
				nodeContainer.sculkRadio$setNode(node);
			}
			return;
		}
		// create new one
		node = switch (blockEntity) {
			case SculkSensorBlockEntity be
				when state.is(Blocks.NOTE_BLOCK)
					-> new SpeakerNode(be.getBlockPos());
			case SculkShriekerBlockEntity be
				when state.is(Blocks.JUKEBOX)
					-> new RadioNode(be.getBlockPos());
			case CalibratedSculkSensorBlockEntity be
				when state.is(Blocks.AMETHYST_BLOCK)
				&& SculkRadio.antennasEnabled
					-> new AntennaNode(be.getBlockPos());
			case SculkSensorBlockEntity be
				when state.is(Blocks.AMETHYST_BLOCK)
					-> new RelayNode(be.getBlockPos());
			case SculkShriekerBlockEntity be
				when state.is(Blocks.SCULK_CATALYST)
				&& SculkRadio.microphonesEnabled()
					-> new MicrophoneNode(be.getBlockPos());
			case SculkShriekerBlockEntity be
				when state.is(Blocks.TARGET)
				&& SculkRadio.redstoneEnabled
					-> new RedstoneSourceNode(be.getBlockPos());
			case SculkSensorBlockEntity be
				when state.is(Blocks.TARGET)
				&& SculkRadio.redstoneEnabled
					-> new RedstoneReceiverNode(be.getBlockPos());
			default -> null;
		};
		if (node != null) {
			boolean added = NodeRegistry.of(level).register(node);
			if (added && blockEntity instanceof INodeContainer nodeContainer) {
				nodeContainer.sculkRadio$setNode(node);
			}
		}
	}

	@Nullable
	public static Node getFromBlockEntity(@Nullable BlockEntity blockEntity) {
		if (blockEntity instanceof INodeContainer nodeContainer) {
			return nodeContainer.sculkRadio$getNode();
		}
		return null;
	}

	public static void removeFromBlockEntity(@Nullable BlockEntity blockEntity) {
		if (!(blockEntity instanceof INodeContainer nodeContainer)) return;
		var node = nodeContainer.sculkRadio$getNode();
		if (node == null) return;
		NodeRegistry.of((ServerLevel) blockEntity.getLevel()).remove(node);
		nodeContainer.sculkRadio$setNode(null);
	}

}
