package nl.gjorgdy.sculk_radio.listeners;

import net.fabricmc.fabric.api.event.player.BlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.nodes.AntennaNode;
import nl.gjorgdy.sculk_radio.utils.VisualUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class OnUseListener implements BlockEvents.UseWithoutItemCallback, BlockEvents.UseItemOnCallback {

	@Override
	public @Nullable InteractionResult useWithoutItem(@NonNull BlockState blockState, Level level, @NonNull BlockPos blockPos, @NonNull Player player, @NonNull BlockHitResult blockHitResult) {
		if (level.isClientSide()) return null;
		var blockEntity = level.getBlockEntity(blockPos);
		if (blockEntity instanceof INodeContainer nodeContainer) {
			var node = nodeContainer.sculkRadio$getNode();
			if (node != null) {
				node.getNeighbours().forEach(
					neighbour -> {
						if (node instanceof AntennaNode && neighbour instanceof AntennaNode) {
							VisualUtils.spawnVibrationParticles((ServerLevel) level, node.getPos(), node.getPos().above(16));
							VisualUtils.spawnAntennaParticles((ServerLevel) level, node.getPos());
						}
						else if (node.canTransmit() && neighbour.canReceive()) {
							VisualUtils.spawnVibrationParticles((ServerLevel) level, node.getPos(), neighbour.getPos());
						} else if (node.canReceive() && neighbour.canTransmit()) {
							VisualUtils.spawnVibrationParticles((ServerLevel) level, neighbour.getPos(), node.getPos());
						}
					}
				);
				player.swing(player.getUsedItemHand(), true);
				return InteractionResult.SUCCESS;
			}
		}
		return null;
	}

	@Override
	public @Nullable InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState blockState, Level level, @NonNull BlockPos blockPos, @NonNull Player player, @NonNull InteractionHand interactionHand, @NonNull BlockHitResult blockHitResult) {
		if (level.isClientSide() || !itemStack.is(Items.AMETHYST_SHARD)) return null;
		var blockEntity = level.getBlockEntity(blockPos);
		if (blockEntity instanceof INodeContainer nodeContainer) {
			var node = nodeContainer.sculkRadio$getNode();
			if (node instanceof AntennaNode) {
				System.out.println("Frequency: " + level.getRandom().nextInt(0, 1_000_000));
				player.swing(player.getUsedItemHand(), true);
				return InteractionResult.SUCCESS;
			}
		}
		return null;
	}
}
