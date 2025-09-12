package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.block.entity.SculkSensorBlockEntity$VibrationCallback")
public abstract class SculkSensorVibrationCallbackMixin {

    @Shadow @Final protected BlockPos pos;

    @Inject(method = "accepts", at = @At("RETURN"), cancellable = true)
    public void accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockBelow = world.getBlockState(this.pos.down());
        if (blockBelow.isOf(Blocks.NOTE_BLOCK)
                || blockBelow.isOf(Blocks.AMETHYST_BLOCK) && !isResonateEvent(event)) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean isResonateEvent(RegistryEntry<GameEvent> holder) {
        return holder == GameEvent.RESONATE_1
                || holder == GameEvent.RESONATE_2
                || holder == GameEvent.RESONATE_3
                || holder == GameEvent.RESONATE_4
                || holder == GameEvent.RESONATE_5
                || holder == GameEvent.RESONATE_6
                || holder == GameEvent.RESONATE_7
                || holder == GameEvent.RESONATE_8
                || holder == GameEvent.RESONATE_9
                || holder == GameEvent.RESONATE_10
                || holder == GameEvent.RESONATE_11
                || holder == GameEvent.RESONATE_12
                || holder == GameEvent.RESONATE_13
                || holder == GameEvent.RESONATE_14
                || holder == GameEvent.RESONATE_15;
    }
}
