package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SculkSensorBlockEntity.class)
public abstract class SculkSensorBlockEntityMixin extends BlockEntity implements INodeContainer {

    @Unique
    private Node node;

    public SculkSensorBlockEntityMixin(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    @Override
    public void sculkRadio$setNode(Node node) {
        this.node = node;
    }

    @Override
    public Node sculkRadio$getNode() {
        if (node == null) {
            this.node = SculkRadio.getNode((ServerLevel) this.getLevel(), this.getBlockPos());
        }
        return node;
    }

}
