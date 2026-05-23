package nl.gjorgdy.sculk_radio.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.TypedInstance;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class AbstractBlockStateMixin extends StateHolder<Block, BlockState> implements TypedInstance<Block> {

    protected AbstractBlockStateMixin(Block owner, Property<?>[] propertyKeys, Comparable<?>[] propertyValues) {
        super(owner, propertyKeys, propertyValues);
    }

    @WrapMethod(method = "getDirectSignal")
    public int redstonePower(BlockGetter level, BlockPos pos, Direction direction, Operation<Integer> original) {
//        if ((is(Blocks.NOTE_BLOCK) || is(Blocks.AMETHYST_BLOCK)) && level.getBlockEntity(pos.above()) instanceof INodeContainer nodeContainer) {
//            var node = nodeContainer.sculkRadio$getNode();
//            return node != null && node.isActive() ? 15 : original.call(level, pos, direction);
//        }
        return original.call(level, pos, direction);
    }

}
