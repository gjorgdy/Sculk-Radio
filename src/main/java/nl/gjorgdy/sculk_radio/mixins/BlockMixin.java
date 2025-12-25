package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.gjorgdy.sculk_radio.NodeRegistry;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.Node;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "onBreak", at = @At("RETURN"))
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<BlockState> cir) {
        if (world.isClient()) return;
        if (world.getBlockEntity(pos.up()) instanceof INodeContainer nc) {
            var node = nc.sculkRadio$getNode();
            if (node != null) NodeRegistry.INSTANCE.removeNode(node);
        }
    }

    @Inject(method = "onPlaced", at = @At("RETURN"))
    public void onPlace(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        if (world.isClient()) return;
        var blockEntity = world.getBlockEntity(pos.up());
        if (!(blockEntity instanceof INodeContainer nc)) return;
        Node node = null;
        // calibrated receiver
        if (state.isOf(Blocks.NOTE_BLOCK) && blockEntity instanceof CalibratedSculkSensorBlockEntity) {
            node = NodeRegistry.INSTANCE.registerCalibratedReceiverNode((ServerWorld) world, pos.up());
        }
        // receiver
        else if (state.isOf(Blocks.NOTE_BLOCK) && blockEntity instanceof SculkSensorBlockEntity) {
            node = NodeRegistry.INSTANCE.registerReceiverNode((ServerWorld) world, pos.up());
        }
        // repeater
        if (state.isOf(Blocks.AMETHYST_BLOCK) && blockEntity instanceof SculkSensorBlockEntity) {
            node = NodeRegistry.INSTANCE.registerRepeaterNode((ServerWorld) world, pos.up());
        }
        // source node
        if (state.isOf(Blocks.JUKEBOX) && blockEntity instanceof SculkShriekerBlockEntity) {
            node = NodeRegistry.INSTANCE.registerSourceNode((ServerWorld) world, pos.up());
        }
        if (node != null) nc.sculkRadio$setNode(node);
    }

}
