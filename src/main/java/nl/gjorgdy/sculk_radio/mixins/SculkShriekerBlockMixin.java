package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import nl.gjorgdy.sculk_radio.interfaces.NodeContainer;
import nl.gjorgdy.sculk_radio.objects.SourceNode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = SculkShriekerBlock.class)
public class SculkShriekerBlockMixin extends Block {

    public SculkShriekerBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.getBlockEntity(pos) instanceof NodeContainer nc && nc.sculkRadio$getNode() instanceof SourceNode sn) {
            sn.updateFrequency();
        }
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
    }

}
