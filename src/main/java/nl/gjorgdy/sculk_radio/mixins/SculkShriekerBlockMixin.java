package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.BlockState;
import nl.gjorgdy.sculk_radio.connections.SculkChannel;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SculkShriekerBlock.class)
public abstract class SculkShriekerBlockMixin extends BaseEntityBlock {

    protected SculkShriekerBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    protected void updateIndirectNeighbourShapes(@NonNull BlockState state, LevelAccessor level, @NonNull BlockPos pos, @UpdateFlags int updateFlags, int updateLimit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof INodeContainer nc && nc.sculkRadio$getNode() instanceof SculkChannel.ChannelNode channelNode) {
            channelNode.updateFrequency();
        }
        super.updateIndirectNeighbourShapes(state, level, pos, updateFlags, updateLimit);
    }

    @Inject(method = "stepOn", at = @At("HEAD"), cancellable = true)
    public void onSteppedOn(Level level, BlockPos pos, BlockState onState, Entity entity, CallbackInfo ci) {
        if (level.getBlockEntity(pos) instanceof INodeContainer nc && nc.sculkRadio$getNode() instanceof SculkChannel.ChannelNode) {
            ci.cancel();
        }
    }

}
