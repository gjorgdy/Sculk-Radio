package nl.gjorgdy.sculk_radio.mixins.node_containers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ SculkSensorBlockEntity.class, CalibratedSculkSensorBlockEntity.class })
public abstract class SculkSensorBlockEntityMixin extends BlockEntity implements INodeContainer {

    @Unique
    private Node node;

    public SculkSensorBlockEntityMixin(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    @Override
    public void sculkRadio$setNode(Node node) {
        if (level == null || level.isClientSide()) return;
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
        if (level == null || level.isClientSide()) return null;
        if (node == null || node.wasRemoved()) {
            sculkRadio$setNode(SculkRadio.getNode((ServerLevel) this.getLevel(), this.getBlockPos()));
        }
        return node;
    }

}
