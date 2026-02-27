package nl.gjorgdy.sculk_radio.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Shadow
    public abstract boolean isOf(Block block);

    @WrapMethod(method = "getWeakRedstonePower")
    public int redstonePower(BlockView world, BlockPos pos, Direction direction, Operation<Integer> original) {
        if ((isOf(Blocks.NOTE_BLOCK) || isOf(Blocks.AMETHYST_BLOCK)) && world.getBlockEntity(pos.up()) instanceof INodeContainer nodeContainer) {
            var node = nodeContainer.sculkRadio$getNode();
            return node != null && node.isActive() ? 15 : original.call(world, pos, direction);
        }
        return original.call(world, pos, direction);
    }

}
