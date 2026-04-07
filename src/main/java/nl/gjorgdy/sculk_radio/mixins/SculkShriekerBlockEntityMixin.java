package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import nl.gjorgdy.sculk_radio.interfaces.INodeContainer;
import nl.gjorgdy.sculk_radio.objects.Node;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(at = @At("HEAD"), method = "shriek", cancellable = true)
    public void shriekPlayer(ServerLevel level, Entity sourceEntity, CallbackInfo ci) {
        if (node != null) ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "shriek", cancellable = true)
    public void shriekEntity(ServerLevel level, Entity sourceEntity, CallbackInfo ci) {
        if (node != null) ci.cancel();
    }

}
