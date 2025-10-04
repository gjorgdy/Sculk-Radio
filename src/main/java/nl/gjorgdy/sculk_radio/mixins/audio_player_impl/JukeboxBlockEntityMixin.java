package nl.gjorgdy.sculk_radio.mixins.audio_player_impl;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.SculkRadio;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(
        require = {
                @Condition(value = "audioplayer"),
                @Condition(value = "voicechat")
        }
)
@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin extends BlockEntity {

    public JukeboxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "setStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/jukebox/JukeboxManager;stopPlaying(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/block/BlockState;)V"))
    public void onStop(ItemStack stack, CallbackInfo ci) {
        if (this.world instanceof ServerWorld sw)
            SculkRadio.api().disconnect(sw, this.pos);
    }

}
