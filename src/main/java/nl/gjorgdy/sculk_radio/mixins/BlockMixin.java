package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.gjorgdy.sculk_radio.NodeRegistry;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.Node;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "destroy", at = @At("RETURN"))
    public void onBreak(LevelAccessor level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (level.isClientSide()) return;
        if (level.getBlockEntity(pos.above()) instanceof INodeContainer nc) {
            var node = nc.sculkRadio$getNode();
            if (node != null) NodeRegistry.INSTANCE.removeNode(node);
        }
    }

    @Inject(method = "setPlacedBy", at = @At("RETURN"))
    public void onPlace(Level level, BlockPos pos, BlockState state, LivingEntity by, ItemStack itemStack, CallbackInfo ci) {
        if (level.isClientSide()) return;
        var blockEntity = level.getBlockEntity(pos.above());
        if (!(blockEntity instanceof INodeContainer nc)) return;
        Node node = null;
        // calibrated receiver
        if (state.is(Blocks.NOTE_BLOCK) && blockEntity instanceof CalibratedSculkSensorBlockEntity) {
            node = NodeRegistry.INSTANCE.registerCalibratedReceiverNode((ServerLevel) level, pos.above());
        }
        // receiver
        else if (state.is(Blocks.NOTE_BLOCK) && blockEntity instanceof SculkSensorBlockEntity) {
            node = NodeRegistry.INSTANCE.registerReceiverNode((ServerLevel) level, pos.above());
        }
        // repeater
        if (state.is(Blocks.AMETHYST_BLOCK) && blockEntity instanceof SculkSensorBlockEntity) {
            node = NodeRegistry.INSTANCE.registerRepeaterNode((ServerLevel) level, pos.above());
        }
        // source node
        if (state.is(Blocks.JUKEBOX) && blockEntity instanceof SculkShriekerBlockEntity) {
            node = NodeRegistry.INSTANCE.registerSourceNode((ServerLevel) level, pos.above());
        }
        if (node != null) nc.sculkRadio$setNode(node);
    }

}
