package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.SourceNode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SculkShriekerBlock.class)
public class SculkShriekerBlockMixin extends Block {

    public SculkShriekerBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!world.isClient() && world.getBlockEntity(pos) instanceof INodeContainer nc && nc.sculkRadio$getNode() instanceof SourceNode sn) {
            sn.updateFrequency();
        }
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
    }

    @Inject(method = "onSteppedOn", at = @At("HEAD"), cancellable = true)
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        if (world.getBlockEntity(pos) instanceof INodeContainer nc && nc.sculkRadio$getNode() instanceof SourceNode sn) {
            ci.cancel();
        }
    }

}
