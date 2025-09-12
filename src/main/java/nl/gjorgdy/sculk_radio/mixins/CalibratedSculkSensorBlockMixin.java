package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CalibratedSculkSensorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import nl.gjorgdy.sculk_radio.interfaces.NodeContainer;
import nl.gjorgdy.sculk_radio.objects.ReceiverNode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = CalibratedSculkSensorBlock.class)
public class CalibratedSculkSensorBlockMixin extends Block {

    public CalibratedSculkSensorBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.getBlockEntity(pos) instanceof NodeContainer nc && nc.sculkRadio$getNode() instanceof ReceiverNode rn) {
            rn.updateFrequency();
        }
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
    }

}
