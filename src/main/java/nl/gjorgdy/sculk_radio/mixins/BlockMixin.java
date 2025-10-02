package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.gjorgdy.sculk_radio.NodeRegistry;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "afterBreak", at = @At("HEAD"))
    public void onBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (blockEntity instanceof INodeContainer nc) {
            NodeRegistry.INSTANCE.removeNode(nc.sculkRadio$getNode());
        }
    }

}
