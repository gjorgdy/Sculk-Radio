package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

public class RepeaterNode extends TransmittingNode {

    public RepeaterNode(ServerWorld world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public void internalTick() {
        ParticleUtils.activateSensor(this);
        super.internalTick();
        getWorld().updateNeighbors(getPos().down(), Blocks.AMETHYST_BLOCK);
    }

    @Override
    public void disconnect() {
        ParticleUtils.deactivateSensor(this);
        super.disconnect();
        getWorld().updateNeighbors(getPos().down(), Blocks.AMETHYST_BLOCK);
    }
}
