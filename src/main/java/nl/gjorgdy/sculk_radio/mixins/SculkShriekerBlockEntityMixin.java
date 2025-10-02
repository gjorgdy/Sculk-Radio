package nl.gjorgdy.sculk_radio.mixins;

import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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

    @Inject(at = @At("HEAD"), method = "shriek(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity;)V", cancellable = true)
    public void shriekPlayer(ServerWorld world, ServerPlayerEntity player, CallbackInfo ci) {
        if (node != null) ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "shriek(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;)V", cancellable = true)
    public void shriekEntity(ServerWorld world, Entity entity, CallbackInfo ci) {
        if (node != null) ci.cancel();
    }

}
