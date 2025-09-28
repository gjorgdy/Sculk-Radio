package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.jukebox.JukeboxManager;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import nl.gjorgdy.sculk_radio.interfaces.NodeContainer;
import nl.gjorgdy.sculk_radio.objects.SourceNode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @Unique
    private SourceNode sourceNode;

    @Shadow
    private static void spawnNoteParticles(WorldAccess worldAccess, BlockPos blockPos) {
    }

    @Unique
    @Nullable
    public SourceNode getSourceNode(WorldAccess world) {
        if (world.isClient()) return null;
        if (sourceNode == null) {
            var up = world.getBlockEntity(this.pos.up());
            if (up instanceof NodeContainer nc && nc.sculkRadio$getNode() instanceof SourceNode sn) {
                sourceNode = sn;
            }
        }
        return sourceNode;
    }

    @Inject(method = "startPlaying", at = @At("HEAD"), cancellable = true)
    public void onStartPlaying(WorldAccess world, RegistryEntry<JukeboxSong> song, CallbackInfo ci) {
        var sn = getSourceNode(world);
        if (sn == null) return;
        sn.play(
                n -> play(world, song, n.getPos()),
                n -> stop(world, n.getPos())
        );
        ci.cancel();
    }

    @Inject(method = "stopPlaying", at = @At("HEAD"), cancellable = true)
    public void onStopPlaying(WorldAccess world, BlockState state, CallbackInfo ci) {
        var sn = getSourceNode(world);
        if (sn == null) return;
        sn.stop();
        ci.cancel();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/jukebox/JukeboxManager;spawnNoteParticles(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)V"))
    public void onSpawnNotes(WorldAccess world, BlockPos pos) {
        var sn = getSourceNode(world);
        if (sn == null) spawnNoteParticles(world, pos);
        else sn.playTick();
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
        if (this.song != null) {
            this.song = null;
            this.ticksSinceSongStarted = 0L;
        }
        world.emitGameEvent(GameEvent.JUKEBOX_STOP_PLAY, pos, GameEvent.Emitter.of(world.getBlockState(pos)));
        world.syncWorldEvent(1011, pos, 0);
        this.changeNotifier.notifyChange();
    }

}
