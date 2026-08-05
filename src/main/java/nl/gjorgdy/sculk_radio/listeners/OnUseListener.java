package nl.gjorgdy.sculk_radio.listeners;

import net.fabricmc.fabric.api.event.player.BlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.nodes.AntennaNode;
import nl.gjorgdy.sculk_radio.registries.FrequencyRegistry;
import nl.gjorgdy.sculk_radio.utils.ItemUtils;
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
			if (node != null && !node.wasRemoved()) {
				node.pulseNeighbours();
				player.swing(player.getUsedItemHand(), true);
				return InteractionResult.SUCCESS;
			}
		}
		return null;
	}

	@Override
	public @Nullable InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState blockState, Level level, @NonNull BlockPos blockPos, @NonNull Player player, @NonNull InteractionHand interactionHand, @NonNull BlockHitResult blockHitResult) {
		if (level.isClientSide() || !itemStack.is(Items.AMETHYST_SHARD) || !SculkRadio.tuningEnabled) return null;
		var blockEntity = level.getBlockEntity(blockPos);
		if (blockEntity instanceof INodeContainer nodeContainer) {
			var node = nodeContainer.sculkRadio$getNode();
			if (node instanceof AntennaNode antennaNode) {
				var existingFrequency = ItemUtils.getFrequency(itemStack);
				// Tune shard from antenna
				if (existingFrequency.isEmpty() && antennaNode.getFrequency() > 15) {
					ItemUtils.setFrequency(itemStack, antennaNode.getFrequency());
					feedback(level, blockPos, player, antennaNode.getFrequency(), false, true);
				// Tune antenna from shard
				} else if (existingFrequency.isPresent()) {
					antennaNode.setFrequency(existingFrequency.get());
					feedback(level, blockPos, player, existingFrequency.get(), true, false);
				// Create a new frequency and tune antenna and shard
				} else {
					var frequencyRegistry = FrequencyRegistry.of((ServerLevel) level);
					var newFrequency = frequencyRegistry.getNewFrequency();
					antennaNode.setFrequency(newFrequency);
					ItemUtils.setFrequency(itemStack, newFrequency);
					feedback(level, blockPos, player, newFrequency, true, true);
				}
				return InteractionResult.SUCCESS;
			}
		}
		return null;
	}

	private void feedback(Level level, BlockPos blockPos, Player player, int frequency, boolean antenna, boolean shard) {
		String obj;
		if (antenna && shard) obj = "antenna & shard";
		else if (antenna) obj = "antenna";
		else obj = "shard";
		player.sendOverlayMessage(
			Component.literal("Tuned " + obj + " to " + Integer.toHexString(frequency).toUpperCase())
		);
		level.playSound(null, blockPos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0f, 1.0f);
		player.swing(player.getUsedItemHand(), true);
	}
}
