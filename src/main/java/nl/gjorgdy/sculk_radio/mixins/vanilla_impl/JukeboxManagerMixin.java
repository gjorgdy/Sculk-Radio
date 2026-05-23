package nl.gjorgdy.sculk_radio.mixins.vanilla_impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.nodes.RadioNode;
import nl.gjorgdy.sculk_radio.nodes.SpeakerNode;
import nl.gjorgdy.sculk_radio.utils.NodeUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxSongPlayer.class)
public class JukeboxManagerMixin {

    @Shadow
    @Nullable
    private Holder<JukeboxSong> song;

    @Shadow
    private long ticksSinceSongStarted;

    @Shadow
    @Final
    private BlockPos blockPos;

    @Shadow
    @Final
    private JukeboxSongPlayer.OnSongChanged onSongChanged;

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    public void onStartPlaying(LevelAccessor level, Holder<JukeboxSong> song, CallbackInfo ci) {
        if (level instanceof ServerLevel sw) {
            var node = NodeUtils.getFromBlockEntity(sw.getBlockEntity(this.blockPos.above()));
            if (node instanceof RadioNode radio) {
                radio.play(
                    speaker -> play(level, song, speaker.getPos().below()),
                    speaker -> stop(level, speaker.getPos().below())
                );
                ci.cancel();
            }
        }
    }

    @Inject(method = "stop", at = @At("HEAD"), cancellable = true)
    public void onStopPlaying(LevelAccessor level, BlockState blockState, CallbackInfo ci) {
        if (level instanceof ServerLevel sw) {
            var node = NodeUtils.getFromBlockEntity(sw.getBlockEntity(this.blockPos.above()));
            if (node instanceof RadioNode radio) {
                radio.stop();
                if (this.song != null) {
                    this.song = null;
                    this.ticksSinceSongStarted = 0L;
                }
                ci.cancel();
            }
        }
    }

    @Unique
    public void play(LevelAccessor level, Holder<JukeboxSong> song, BlockPos pos) {
        this.song = song;
        this.ticksSinceSongStarted = 0L;
        int songId = level.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG).getId(song.value());
        level.levelEvent(1010, pos, songId);
        this.onSongChanged.notifyChange();
    }

    @Unique
    public void stop(LevelAccessor level, BlockPos pos) {
        level.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, pos, GameEvent.Context.of(level.getBlockState(pos)));
        level.levelEvent(1011, pos, 0);
        this.onSongChanged.notifyChange();
    }

}
