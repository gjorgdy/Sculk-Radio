package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CalibratedSculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.CalibratedReceiverNode;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = CalibratedSculkSensorBlock.class)
public class CalibratedSculkSensorBlockMixin extends Block {

    public CalibratedSculkSensorBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    protected void neighborChanged(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Block block, @org.jspecify.annotations.Nullable Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide()) return;
        if (level.getBlockEntity(pos) instanceof INodeContainer nc && nc.sculkRadio$getNode() instanceof CalibratedReceiverNode rn) {
            rn.updateFrequency();
        }
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
    }

}
