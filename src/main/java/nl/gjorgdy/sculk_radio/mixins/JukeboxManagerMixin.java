package nl.gjorgdy.sculk_radio.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.jukebox.JukeboxManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import nl.gjorgdy.sculk_radio.SculkRadio;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxManager.class)
public class JukeboxManagerMixin {

    @Inject(method = "spawnNoteParticles",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"), cancellable = true)
    private static void onSpawnNoteParticles(WorldAccess world, BlockPos pos, CallbackInfo ci, @Local ServerWorld serverWorld) {
        if (world.isClient()) return;
        boolean executed = SculkRadio.api().tick(serverWorld, pos);
        if (executed) ci.cancel();
    }

}
