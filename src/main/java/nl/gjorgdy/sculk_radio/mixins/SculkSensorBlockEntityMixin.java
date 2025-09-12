package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.block.entity.SculkSensorBlockEntity;
import nl.gjorgdy.sculk_radio.interfaces.NodeContainer;
import nl.gjorgdy.sculk_radio.objects.Node;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SculkSensorBlockEntity.class)
public abstract class SculkSensorBlockEntityMixin implements NodeContainer {

    @Unique
    private Node node;

    @Override
    public void sculkRadio$setNode(Node node) {
        this.node = node;
    }

    @Override
    public Node sculkRadio$getNode() {
        return node;
    }

}
