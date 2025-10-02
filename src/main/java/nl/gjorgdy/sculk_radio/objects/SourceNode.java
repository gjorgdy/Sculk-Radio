package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import nl.gjorgdy.sculk_radio.NodeRegistry;
import nl.gjorgdy.sculk_radio.interfaces.ICalibrated;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

import java.util.function.Consumer;

public class SourceNode extends TransmittingNode implements ICalibrated {

    protected int frequency;

    public SourceNode(ServerWorld world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getFrequency() {
        return frequency;
    }

    @Override
    public void updateFrequency() {
        this.frequency =
                Math.max(
                        getWorld().getEmittedRedstonePower(getPos().north(), Direction.NORTH),
                        Math.max(getWorld().getEmittedRedstonePower(getPos().east(), Direction.EAST),
                                Math.max(
                                        getWorld().getEmittedRedstonePower(getPos().south(), Direction.SOUTH),
                                        getWorld().getEmittedRedstonePower(getPos().west(), Direction.WEST)
                                )
                        )
                );
    }

    @Override
    public void initiate(Consumer<Node> connectCallback, Consumer<Node> disconnectCallback) {
        updateFrequency();
        NodeRegistry.INSTANCE.connectNodes(this);
        if (!isConnected()) disconnect();
        receivers.forEach(n -> n.initiate(connectCallback, disconnectCallback));
    }

    @Override
    public boolean isConnected() {
        return !receivers.isEmpty();
    }

    @Override
    public void internalTick() {
        ParticleUtils.spawnShriekerParticles(this);
        super.internalTick();
    }
}
