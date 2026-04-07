package nl.gjorgdy.sculk_radio.compat.mixins.audio_player_impl;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.LevelAccessor;
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
@Mixin(JukeboxSongPlayer.class)
public abstract class JukeboxManagerMixin {


    @Shadow
    public abstract boolean isPlaying();

    @Shadow
    @Final
    private BlockPos blockPos;

    @Inject(method = "tick", at = @At(value = "HEAD"), order = 1001)
    public void tick(LevelAccessor level, net.minecraft.world.level.block.state.BlockState blockState, CallbackInfo ci) {
        if (!isPlaying() && level instanceof ServerLevel serverWorld) {
            SculkRadio.api().disconnect(serverWorld, blockPos);
        }
    }

}
