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
import nl.gjorgdy.sculk_radio.nodes.Node;
import org.jspecify.annotations.Nullable;

public abstract class NodeUtils {

	@Nullable
	public static Node register(ServerLevel level, BlockState state, BlockEntity blockEntity) {
		var registry = SculkRadio.getLevelRegistry().getNodeRegistry(level);
		var node = switch (blockEntity) {
			// speaker
			case SculkSensorBlockEntity be when state.is(Blocks.NOTE_BLOCK) -> registry.registerSpeaker(be.getBlockPos());
			// radio
			case SculkShriekerBlockEntity be when state.is(Blocks.JUKEBOX) -> registry.registerRadio(be.getBlockPos());
			// relay
			case SculkSensorBlockEntity be when state.is(Blocks.AMETHYST_BLOCK) -> registry.registerRelay(be.getBlockPos());
			// antenna
			case CalibratedSculkSensorBlockEntity be when state.is(Blocks.AMETHYST_BLOCK) -> registry.registerAntenna(be.getBlockPos());
			default -> null;
		};
		if (node != null && blockEntity instanceof INodeContainer nodeContainer) {
			nodeContainer.sculkRadio$setNode(node);
		}
		return node;
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
		var registry = SculkRadio.getLevelRegistry().getNodeRegistry((ServerLevel) blockEntity.getLevel());
		registry.removeNode(node);
		nodeContainer.sculkRadio$setNode(null);
	}

}
