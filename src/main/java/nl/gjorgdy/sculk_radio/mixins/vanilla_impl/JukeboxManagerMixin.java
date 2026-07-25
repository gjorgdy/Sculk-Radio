package nl.gjorgdy.sculk_radio.mixins.vanilla_impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import nl.gjorgdy.sculk_radio.nodes.Node;
import nl.gjorgdy.sculk_radio.nodes.audio.RadioNode;
import nl.gjorgdy.sculk_radio.nodes.audio.SpeakerNode;
import nl.gjorgdy.sculk_radio.streams.Stream;
import nl.gjorgdy.sculk_radio.utils.NodeUtils;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;
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
        if (level instanceof ServerLevel serverLevel) {
            var node = NodeUtils.getFromBlockEntity(serverLevel.getBlockEntity(this.blockPos.above()));
            if (node instanceof RadioNode radio) {
                this.song = song;
                this.ticksSinceSongStarted = 0L;
                radio.start(sn -> createStream(level, song, sn));
                this.onSongChanged.notifyChange();
                ci.cancel();
            }
        }
    }

    @Inject(method = "stop", at = @At("HEAD"), cancellable = true)
    public void onStopPlaying(LevelAccessor level, BlockState blockState, CallbackInfo ci) {
        if (level instanceof ServerLevel serverLevel) {
            var node = NodeUtils.getFromBlockEntity(serverLevel.getBlockEntity(this.blockPos.above()));
            if (node instanceof RadioNode radio) {
                radio.stop();
                radio.particleTick(serverLevel);
                this.onSongChanged.notifyChange();
                if (this.song != null) {
                    this.song = null;
                    this.ticksSinceSongStarted = 0L;
                }
                ci.cancel();
            }
        }
    }

    @Unique
    private Stream createStream(LevelAccessor level, Holder<JukeboxSong> song, Node source) {
        int songId = level.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG).getId(song.value());
        return new Stream(
            n -> n instanceof SpeakerNode,
            n -> level.levelEvent(1010, n.getPos(), songId), // connect
            n -> level.levelEvent(1011, n.getPos(), 0), // disconnect
            source,
            false
        );
    }

}
