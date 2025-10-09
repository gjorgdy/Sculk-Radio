package nl.gjorgdy.sculk_radio.compat.mixins.audio_player_impl;

import de.maxhenkel.audioplayer.audioplayback.PlayerThread;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import nl.gjorgdy.sculk_radio.compat.audio_player.MultiLocationalAudioChannel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
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
@Mixin(PlayerThread.class)
public class PlayerThreadMixin<T extends AudioChannel> {

    @Mutable
    @Shadow
    @Final
    @Nullable
    private Runnable onStopped;

    @Shadow
    @Final
    private T audioChannel;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onConstructor(AudioChannel audioChannel, Runnable onStopped, CallbackInfo ci) {
        this.onStopped = () -> {
            onStopped.run();
            if (this.audioChannel instanceof MultiLocationalAudioChannel multiLocationalAudioChannel) {
                multiLocationalAudioChannel.onStop();
            }
        };
    }

}
