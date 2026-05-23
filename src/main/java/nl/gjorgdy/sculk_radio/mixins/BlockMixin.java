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
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.nodes.Node;
import nl.gjorgdy.sculk_radio.utils.NodeUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "destroy", at = @At("RETURN"))
    public void onBreak(LevelAccessor level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (level.isClientSide()) return;
        if (level.getBlockEntity(pos.above()) instanceof INodeContainer nc && level instanceof ServerLevel serverLevel) {
            var node = nc.sculkRadio$getNode();
            if (node != null) SculkRadio.getLevelRegistry()
                    .getNodeRegistry(serverLevel)
                    .removeNode(node);
        }
    }

    @Inject(method = "setPlacedBy", at = @At("RETURN"))
    public void onPlace(Level level, BlockPos pos, BlockState state, LivingEntity by, ItemStack itemStack, CallbackInfo ci) {
        if (level.isClientSide()) return;
        var topBlockEntity = level.getBlockEntity(pos.above());
        if (!(topBlockEntity instanceof INodeContainer nc)) return;

        var node = NodeUtils.register((ServerLevel) level, state, topBlockEntity);
        if (node != null) nc.sculkRadio$setNode(node);
    }

}
