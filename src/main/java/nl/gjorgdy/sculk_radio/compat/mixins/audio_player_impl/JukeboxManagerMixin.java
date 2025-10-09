package nl.gjorgdy.sculk_radio.compat.mixins.audio_player_impl;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.block.BlockState;
import net.minecraft.block.jukebox.JukeboxManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import nl.gjorgdy.sculk_radio.SculkRadio;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(
        require = {
                @Condition(value = "audioplayer"),
                @Condition(value = "voicechat")
        }
)
@Mixin(JukeboxManager.class)
public abstract class JukeboxManagerMixin {


    @Shadow
    public abstract boolean isPlaying();

    @Shadow
    @Final
    private BlockPos pos;

    @Inject(method = "tick", at = @At(value = "HEAD"), order = 1001)
    public void tick(WorldAccess world, BlockState state, CallbackInfo ci) {
        if (!isPlaying() && world instanceof ServerWorld serverWorld) {
            SculkRadio.api().disconnect(serverWorld, pos);
        }
    }

}
