package nl.gjorgdy.sculk_radio.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.nodes.Node;
import nl.gjorgdy.sculk_radio.utils.NodeUtils;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess {

    public LevelChunkMixin(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, PalettedContainerFactory containerFactory, long inhabitedTime, LevelChunkSection @Nullable [] sections, @Nullable BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, containerFactory, inhabitedTime, sections, blendingData);
    }

    @Shadow
    public abstract Level getLevel();

    @Inject(method = "setBlockEntity", at = @At("RETURN"))
    public void onLoadBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
        if (getLevel().isClientSide()) return;
        NodeUtils.register(
            (ServerLevel) getLevel(),
            getBlockState(blockEntity.getBlockPos().below()),
            blockEntity
        );
    }

    @Inject(method = "clearAllBlockEntities", at = @At(value = "INVOKE", target = "Ljava/util/Map;clear()V", ordinal = 0))
    public void onUnloadBlockEntity(CallbackInfo ci) {
        for (var blockEntity : this.blockEntities.values()) {
            if (getLevel().isClientSide()) return;
            NodeUtils.removeFromBlockEntity(blockEntity);
        }
    }

    @Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;removeGameEventListener(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/server/level/ServerLevel;)V"))
    public void onBreakBlockEntity(BlockPos pos, CallbackInfo ci, @Local(name = "removeThis") BlockEntity removeThis) {
        if (getLevel().isClientSide()) return;
        NodeUtils.removeFromBlockEntity(removeThis);
    }

}
