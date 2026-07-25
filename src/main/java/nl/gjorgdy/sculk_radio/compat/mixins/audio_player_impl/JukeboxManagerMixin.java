package nl.gjorgdy.sculk_radio.compat.mixins.audio_player_impl;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.maxhenkel.audioplayer.interfaces.CustomJukeboxSongPlayer;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.RadioNode;
import nl.gjorgdy.sculk_radio.objects.streams.StreamState;
import nl.gjorgdy.sculk_radio.utils.NodeUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Restriction(
        require = {
                @Condition(value = "audioplayer"),
                @Condition(value = "voicechat")
        }
)
@Mixin(JukeboxSongPlayer.class)
public abstract class JukeboxManagerMixin implements CustomJukeboxSongPlayer {

    @Shadow
    public abstract boolean isPlaying();

    @Shadow
    @Final
    private BlockPos blockPos;

    @WrapMethod(method = "tick")
    public void tick(LevelAccessor level, BlockState blockState, Operation<Void> original) {
        if (!isPlaying() && level instanceof ServerLevel serverWorld) {
            var node = NodeUtils.getFromBlockEntity(serverWorld.getBlockEntity(this.blockPos.above()));
            if (node instanceof RadioNode radio && radio.getState() == StreamState.ACTIVE) {
                radio.stop();
                radio.particleTick(serverWorld);
                System.out.println("Stopping radio stream for jukebox at " + this.blockPos);
            }
        }
        original.call(level, blockState);
    }

}
