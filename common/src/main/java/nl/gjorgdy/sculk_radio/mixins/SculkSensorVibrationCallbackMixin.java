package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.utils.BlockUtils;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.level.block.entity.SculkSensorBlockEntity$VibrationUser")
public abstract class SculkSensorVibrationCallbackMixin {

    @Shadow
    @Final
    protected BlockPos blockPos;

    @Inject(method = "canReceiveVibration", at = @At("RETURN"), cancellable = true)
    public void canReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> event, GameEvent.@Nullable Context context, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockBelow = level.getBlockState(this.blockPos.below());
        if (BlockUtils.isNoteblock(blockBelow)
            || blockBelow.is(Blocks.AMETHYST_BLOCK) && !isResonateEvent(event)
            || (blockBelow.is(Blocks.TARGET) && SculkRadio.redstoneEnabled)
            || (blockBelow.is(Blocks.PURPUR_BLOCK) && SculkRadio.teleportEnabled)
        ) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean isResonateEvent(Holder<GameEvent> holder) {
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
