package nl.gjorgdy.sculk_radio.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import nl.gjorgdy.sculk_radio.NodeRegistry;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.Node;
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
        if (!(blockEntity instanceof INodeContainer nc)) return;
        Node node = null;
        // calibrated receiver node
        switch (blockEntity) {
            case CalibratedSculkSensorBlockEntity calibratedSculkSensorBlockEntity when this.getBlockState(blockEntity.getBlockPos().below()).is(
		            Blocks.NOTE_BLOCK) ->
                    node = NodeRegistry.INSTANCE.registerCalibratedReceiverNode((ServerLevel) this.getLevel(), blockEntity.getBlockPos());
            // generic receiver node
            case SculkSensorBlockEntity sculkSensorBlockEntity when this.getBlockState(blockEntity.getBlockPos().below()).is(Blocks.NOTE_BLOCK) ->
                    node = NodeRegistry.INSTANCE.registerReceiverNode((ServerLevel) this.getLevel(), blockEntity.getBlockPos());
            // repeater node
            case SculkSensorBlockEntity sculkSensorBlockEntity when this.getBlockState(blockEntity.getBlockPos().below()).is(Blocks.AMETHYST_BLOCK) ->
                    node = NodeRegistry.INSTANCE.registerRepeaterNode((ServerLevel) this.getLevel(), blockEntity.getBlockPos());
            // source node
            case SculkShriekerBlockEntity sculkShriekerBlockEntity when this.getBlockState(blockEntity.getBlockPos().below()).is(Blocks.JUKEBOX) ->
                    node = NodeRegistry.INSTANCE.registerSourceNode((ServerLevel) this.getLevel(), blockEntity.getBlockPos());
            default -> {
            }
        }
        if (node != null) nc.sculkRadio$setNode(node);
    }

    @Inject(method = "clearAllBlockEntities", at = @At(value = "INVOKE", target = "Ljava/util/Map;clear()V", ordinal = 0))
    public void onUnloadBlockEntity(CallbackInfo ci) {
        for (var blockEntity : this.blockEntities.values()) {
            if (getLevel().isClientSide() || !(blockEntity instanceof INodeContainer nc)) return;
            var node = nc.sculkRadio$getNode();
            if (node != null) {
                NodeRegistry.INSTANCE.removeNode(node, false);
                nc.sculkRadio$setNode(null);
            }
        }
    }

    @Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;removeGameEventListener(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/server/level/ServerLevel;)V"))
    public void onBreakBlockEntity(BlockPos pos, CallbackInfo ci, @Local(name = "removeThis") BlockEntity removeThis) {
        if (getLevel().isClientSide()) return;
        if (removeThis instanceof INodeContainer nc) {
            var node = nc.sculkRadio$getNode();
            if (node != null) {
                NodeRegistry.INSTANCE.removeNode(node);
            }
        }
    }

}
