package nl.gjorgdy.sculk_radio.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.chunk.BlendingData;
import nl.gjorgdy.sculk_radio.NodeRegistry;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.Node;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin extends Chunk {

    public WorldChunkMixin(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, PalettesFactory palettesFactory, long inhabitedTime, ChunkSection @Nullable [] sectionArray, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, heightLimitView, palettesFactory, inhabitedTime, sectionArray, blendingData);
    }

    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    @Shadow
    @Final
    World world;

    @Shadow
    public abstract World getWorld();

    @Inject(method = "setBlockEntity", at = @At("RETURN"))
    public void onLoadBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
        if (getWorld().isClient()) return;
        if (!(blockEntity instanceof INodeContainer nc)) return;
        Node node = null;
        // calibrated receiver node
        switch (blockEntity) {
            case CalibratedSculkSensorBlockEntity calibratedSculkSensorBlockEntity when this.getBlockState(blockEntity.getPos().down()).isOf(Blocks.NOTE_BLOCK) ->
                    node = NodeRegistry.INSTANCE.registerCalibratedReceiverNode((ServerWorld) this.world, blockEntity.getPos());
            // generic receiver node
            case SculkSensorBlockEntity sculkSensorBlockEntity when this.getBlockState(blockEntity.getPos().down()).isOf(Blocks.NOTE_BLOCK) ->
                    node = NodeRegistry.INSTANCE.registerReceiverNode((ServerWorld) this.world, blockEntity.getPos());
            // repeater node
            case SculkSensorBlockEntity sculkSensorBlockEntity when this.getBlockState(blockEntity.getPos().down()).isOf(Blocks.AMETHYST_BLOCK) ->
                    node = NodeRegistry.INSTANCE.registerRepeaterNode((ServerWorld) this.world, blockEntity.getPos());
            // source node
            case SculkShriekerBlockEntity sculkShriekerBlockEntity when this.getBlockState(blockEntity.getPos().down()).isOf(Blocks.JUKEBOX) ->
                    node = NodeRegistry.INSTANCE.registerSourceNode((ServerWorld) this.world, blockEntity.getPos());
            default -> {
            }
        }
        if (node != null) nc.sculkRadio$setNode(node);
    }

    @Inject(method = "clear", at = @At(value = "INVOKE", target = "Ljava/util/Map;clear()V", ordinal = 0))
    public void onUnloadBlockEntity(CallbackInfo ci) {
        for (var blockEntity : this.blockEntities.values()) {
            if (getWorld().isClient() || !(blockEntity instanceof INodeContainer nc)) return;
            var node = nc.sculkRadio$getNode();
            if (node != null) {
                NodeRegistry.INSTANCE.removeNode(node, false);
                nc.sculkRadio$setNode(null);
            }
        }
    }

    @Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;removeGameEventListener(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/server/world/ServerWorld;)V"))
    public void onBreakBlockEntity(BlockPos pos, CallbackInfo ci, @Local BlockEntity blockEntity) {
        if (getWorld().isClient()) return;
        if (blockEntity instanceof INodeContainer nc) {
            var node = nc.sculkRadio$getNode();
            if (node != null) {
                NodeRegistry.INSTANCE.removeNode(node);
            }
        }
    }

}
