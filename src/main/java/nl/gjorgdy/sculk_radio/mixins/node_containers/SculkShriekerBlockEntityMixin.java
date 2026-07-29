package nl.gjorgdy.sculk_radio.mixins.node_containers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkShriekerBlockEntity.class)
public abstract class SculkShriekerBlockEntityMixin extends BlockEntity implements INodeContainer {

    @Unique
    private Node node;

    public SculkShriekerBlockEntityMixin(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    @Override
    public void sculkRadio$setNode(Node node) {
        this.node = node;
        if (this.node != null && level != null) {
            SculkRadio.scheduleNextTick(() -> level.updateNeighborsAt(
                this.getBlockPos().below(),
                level.getBlockState(this.getBlockPos().below()).getBlock()
            ));
        }
    }

    @Override
    public Node sculkRadio$getNode() {
        if (node == null) {
            sculkRadio$setNode(SculkRadio.getNode((ServerLevel) this.getLevel(), this.getBlockPos()));
        }
        return node;
    }

    @Inject(at = @At("HEAD"), method = "shriek", cancellable = true)
    public void shriekPlayer(ServerLevel level, Entity sourceEntity, CallbackInfo ci) {
        if (node != null) ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "shriek", cancellable = true)
    public void shriekEntity(ServerLevel level, Entity sourceEntity, CallbackInfo ci) {
        if (node != null) ci.cancel();
    }

}
