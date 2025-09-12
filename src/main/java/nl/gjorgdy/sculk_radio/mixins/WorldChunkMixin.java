package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import nl.gjorgdy.sculk_radio.NodeRegistry;
import nl.gjorgdy.sculk_radio.enums.NodeTypes;
import nl.gjorgdy.sculk_radio.interfaces.NodeContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {

    @Shadow public abstract BlockState getBlockState(BlockPos pos);

    @Shadow @Final private World world;

    @Inject(method = "setBlockEntity", at = @At("RETURN"))
    public void onLoadBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
        if (blockEntity instanceof SculkSensorBlockEntity && blockEntity instanceof NodeContainer nc) {
            if (this.getBlockState(blockEntity.getPos().down()).isOf(Blocks.NOTE_BLOCK)) {
                var node = NodeRegistry.INSTANCE.registerSourceNode((ServerWorld) this.world, blockEntity.getPos().down(), NodeTypes.RECEIVER);
                nc.sculkRadio$setNode(node);
            }
        }
        if (blockEntity instanceof SculkShriekerBlockEntity && blockEntity instanceof NodeContainer nc) {
            if (this.getBlockState(blockEntity.getPos().down()).isOf(Blocks.JUKEBOX)) {
                var node = NodeRegistry.INSTANCE.registerSourceNode((ServerWorld) this.world, blockEntity.getPos().down(), NodeTypes.SOURCE);
                nc.sculkRadio$setNode(node);
            }
        }
    }

}
