package nl.gjorgdy.sculk_radio.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.TypedInstance;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.ReceiverNode;
import nl.gjorgdy.sculk_radio.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class AbstractBlockStateMixin extends StateHolder<Block, BlockState> implements TypedInstance<Block> {

    protected AbstractBlockStateMixin(Block owner, Property<?>[] propertyKeys, Comparable<?>[] propertyValues) {
        super(owner, propertyKeys, propertyValues);
    }

    @WrapMethod(method = "hasAnalogOutputSignal")
    public boolean hasAnalog(Operation<Boolean> original) {
	    if (!SculkRadio.redstoneEnabled) return original.call();
        return BlockUtils.isNoteblock(this) || original.call();
    }

    @WrapMethod(method = "getAnalogOutputSignal")
    public int redstonePower(Level level, BlockPos pos, Direction direction, Operation<Integer> original) {
		if (level.isClientSide()) return original.call(level, pos, direction);
	    if (!SculkRadio.redstoneEnabled) return original.call(level, pos, direction);
        if (BlockUtils.isNoteblock(this) && level.getBlockEntity(pos.above()) instanceof INodeContainer nodeContainer) {
            var node = nodeContainer.sculkRadio$getNode();
	        return node instanceof ReceiverNode receiverNode
	                ? receiverNode.getAnalogRedstoneSignal()
	                : original.call(level, pos, direction);
        }
        return original.call(level, pos, direction);
    }

    @WrapMethod(method = "getSignal")
    public int redstonePower(BlockGetter level, BlockPos pos, Direction direction, Operation<Integer> original) {
	    if (level instanceof Level && ((Level) level).isClientSide()) return original.call(level, pos, direction);
		if (!SculkRadio.redstoneEnabled) return original.call(level, pos, direction);
        if ((BlockUtils.isNoteblock(this) || is(Blocks.TARGET)) && level.getBlockEntity(pos.above()) instanceof INodeContainer nodeContainer) {
            var node = nodeContainer.sculkRadio$getNode();
	        return node instanceof ReceiverNode receiverNode
	            ? receiverNode.getRedstoneSignal()
	            : original.call(level, pos, direction);
        }
        return original.call(level, pos, direction);
    }

}
