package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.LevelAccessor;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.RadioNode;
import nl.gjorgdy.sculk_radio.utils.NodeUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxSongPlayer.class)
public class JukeboxSongPlayerMixin {

    @Inject(method = "spawnMusicParticles",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"), cancellable = true)
    private static void onSpawnMusicParticles(LevelAccessor level, BlockPos blockPos, CallbackInfo ci) {
        if (level.isClientSide()) return;
        var node = NodeUtils.getFromBlockEntity(level.getBlockEntity(blockPos.above()));
        if (node instanceof RadioNode radio) {
            if (level instanceof ServerLevel serverLevel) radio.particleTick(serverLevel);
            radio.connectionTick();
            ci.cancel();
        }
    }

}
