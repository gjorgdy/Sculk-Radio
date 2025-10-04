package nl.gjorgdy.sculk_radio.mixins.vanilla_impl;

import net.minecraft.block.BlockState;
import net.minecraft.block.jukebox.JukeboxManager;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import nl.gjorgdy.sculk_radio.SculkRadio;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxManager.class)
public class JukeboxManagerMixin {

    @Shadow
    @Final
    private BlockPos pos;

    @Shadow
    @Nullable
    private RegistryEntry<JukeboxSong> song;

    @Shadow
    private long ticksSinceSongStarted;

    @Shadow
    @Final
    private JukeboxManager.ChangeNotifier changeNotifier;

    @Inject(method = "startPlaying", at = @At("HEAD"), cancellable = true)
    public void onStartPlaying(WorldAccess world, RegistryEntry<JukeboxSong> song, CallbackInfo ci) {
        if (world instanceof ServerWorld sw) {
            boolean started = SculkRadio.api().connect(
                    sw,
                    this.pos,
                    n -> play(world, song, n.getPos()),
                    n -> stop(world, n.getPos())
            );
            if (started) ci.cancel();
        }
    }

    @Inject(method = "stopPlaying", at = @At("HEAD"), cancellable = true)
    public void onStopPlaying(WorldAccess world, BlockState state, CallbackInfo ci) {
        if (world instanceof ServerWorld sw) {
            boolean stopped = SculkRadio.api().disconnect(
                    sw,
                    this.pos
            );
            if (stopped) {
                if (this.song != null) {
                    this.song = null;
                    this.ticksSinceSongStarted = 0L;
                }
                ci.cancel();
            }
        }
    }

    @Unique
    public void play(WorldAccess world, RegistryEntry<JukeboxSong> song, BlockPos pos) {
        this.song = song;
        this.ticksSinceSongStarted = 0L;
        int i = world.getRegistryManager().getOrThrow(RegistryKeys.JUKEBOX_SONG).getRawId(song.value());
        world.syncWorldEvent(null, 1010, pos, i);
        this.changeNotifier.notifyChange();
    }

    @Unique
    public void stop(WorldAccess world, BlockPos pos) {
        world.emitGameEvent(GameEvent.JUKEBOX_STOP_PLAY, pos, GameEvent.Emitter.of(world.getBlockState(pos)));
        world.syncWorldEvent(1011, pos, 0);
        this.changeNotifier.notifyChange();
    }

}
