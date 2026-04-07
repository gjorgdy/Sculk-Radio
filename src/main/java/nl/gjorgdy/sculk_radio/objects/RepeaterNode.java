package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

public class RepeaterNode extends TransmittingNode {

    public RepeaterNode(ServerLevel world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public void internalTick() {
        ParticleUtils.activateSensor(this);
        super.internalTick();
        getWorld().updateNeighborsAt(getPos().below(), Blocks.AMETHYST_BLOCK);
    }

    @Override
    public void disconnect() {
        ParticleUtils.deactivateSensor(this);
        super.disconnect();
        getWorld().updateNeighborsAt(getPos().below(), Blocks.AMETHYST_BLOCK);
    }
}
