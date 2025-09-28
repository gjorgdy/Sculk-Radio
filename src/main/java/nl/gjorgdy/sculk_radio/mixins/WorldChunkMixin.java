package nl.gjorgdy.sculk_radio.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import nl.gjorgdy.sculk_radio.NodeRegistry;
import nl.gjorgdy.sculk_radio.interfaces.NodeContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {

    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    @Shadow
    @Final
    World world;

    @Shadow
    public abstract World getWorld();

    @Inject(method = "setBlockEntity", at = @At("RETURN"))
    public void onLoadBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
        // generic receiver node
        if (blockEntity instanceof SculkSensorBlockEntity && blockEntity instanceof NodeContainer nc
                && this.getBlockState(blockEntity.getPos().down()).isOf(Blocks.NOTE_BLOCK)) {
            var node = NodeRegistry.INSTANCE.registerReceiverNode((ServerWorld) this.world, blockEntity.getPos());
            nc.sculkRadio$setNode(node);
        }
        // repeater node
        if (blockEntity instanceof SculkSensorBlockEntity && blockEntity instanceof NodeContainer nc
                && this.getBlockState(blockEntity.getPos().down()).isOf(Blocks.AMETHYST_BLOCK)) {
            var node = NodeRegistry.INSTANCE.registerRepeaterNode((ServerWorld) this.world, blockEntity.getPos());
            nc.sculkRadio$setNode(node);
        }
        // calibrated receiver node
        if (blockEntity instanceof CalibratedSculkSensorBlockEntity && blockEntity instanceof NodeContainer nc
                && this.getBlockState(blockEntity.getPos().down()).isOf(Blocks.NOTE_BLOCK)) {
            var node = NodeRegistry.INSTANCE.registerCalibratedReceiverNode((ServerWorld) this.world, blockEntity.getPos());
            nc.sculkRadio$setNode(node);
        }
        // source node
        if (blockEntity instanceof SculkShriekerBlockEntity && blockEntity instanceof NodeContainer nc
                && this.getBlockState(blockEntity.getPos().down()).isOf(Blocks.JUKEBOX)) {
            var node = NodeRegistry.INSTANCE.registerSourceNode((ServerWorld) this.world, blockEntity.getPos());
            nc.sculkRadio$setNode(node);
        }
    }

    @Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;removeGameEventListener(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/server/world/ServerWorld;)V"))
    public void onBreakBlockEntity(BlockPos pos, CallbackInfo ci, @Local BlockEntity blockEntity) {
        if (blockEntity instanceof NodeContainer nc) {
            var node = nc.sculkRadio$getNode();
            if (node != null) {
                NodeRegistry.INSTANCE.removeNode(node);
                System.out.println("removed node at " + pos);
            }
        }
    }

}
