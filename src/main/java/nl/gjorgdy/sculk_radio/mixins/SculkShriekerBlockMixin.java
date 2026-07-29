package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.MicrophoneNode;
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
    protected void neighborChanged(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Block block, @org.jspecify.annotations.Nullable Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide()) return;
        if (level.getBlockEntity(pos) instanceof INodeContainer nc && nc.sculkRadio$getNode() instanceof MicrophoneNode microphoneNode) {
            microphoneNode.updateState();
        }
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
    }

    @Inject(method = "stepOn", at = @At("HEAD"), cancellable = true)
    public void onSteppedOn(Level level, BlockPos pos, BlockState onState, Entity entity, CallbackInfo ci) {
        if (level.getBlockEntity(pos) instanceof INodeContainer nc && nc.sculkRadio$getNode() instanceof Node) {
            ci.cancel();
        }
    }

}
