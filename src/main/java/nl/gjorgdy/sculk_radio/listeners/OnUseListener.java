package nl.gjorgdy.sculk_radio.listeners;

import net.fabricmc.fabric.api.event.player.BlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class OnUseListener implements BlockEvents.UseWithoutItemCallback {

	@Override
	public @Nullable InteractionResult useWithoutItem(@NonNull BlockState blockState, Level level, @NonNull BlockPos blockPos, @NonNull Player player, @NonNull BlockHitResult blockHitResult) {
		var blockEntity = level.getBlockEntity(blockPos);
		if (blockEntity instanceof INodeContainer nodeContainer) {
			var node = nodeContainer.sculkRadio$getNode();
			if (node != null) {
				node.getNeighbours().forEach(
					neighbour -> {
						if (node.canTransmit() && neighbour.canReceive()) {
							ParticleUtils.spawnVibrationParticles((ServerLevel) level, node.getPos(), neighbour.getPos());
						} else if (node.canReceive() && neighbour.canTransmit()) {
							ParticleUtils.spawnVibrationParticles((ServerLevel) level, neighbour.getPos(), node.getPos());
						}
					}
				);
				player.swing(player.getUsedItemHand(), true);
				return InteractionResult.SUCCESS;
			}
		}
		return null;
	}

}
