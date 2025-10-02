package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.block.entity.SculkShriekerBlockEntity;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.Node;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SculkShriekerBlockEntity.class)
public class SculkShriekerBlockEntityMixin implements INodeContainer {

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
