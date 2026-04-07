package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.NodeRegistry;
import nl.gjorgdy.sculk_radio.interfaces.ICalibrated;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

import java.util.function.Consumer;

public class SourceNode extends TransmittingNode implements ICalibrated {

    protected int frequency;

    public SourceNode(ServerLevel world, BlockPos pos) {
        super(world, pos);
        onInitialize();
    }

    @Override
    public int getFrequency() {
        return frequency;
    }

    @Override
    public void updateFrequency() {
        this.frequency =
                Math.max(
                        getWorld().getDirectSignal(getPos().north(), Direction.NORTH),
                        Math.max(getWorld().getDirectSignal(getPos().east(), Direction.EAST),
                                Math.max(
                                        getWorld().getDirectSignal(getPos().south(), Direction.SOUTH),
                                        getWorld().getDirectSignal(getPos().west(), Direction.WEST)
                                )
                        )
                );
    }

    public void initialize(Consumer<Node> connectCallback, Consumer<Node> disconnectCallback, Consumer<Node> tickCallback) {
        internalInitialize(connectCallback, disconnectCallback, tickCallback);
    }

    @Override
    public void internalInitialize(Consumer<Node> connectCallback, Consumer<Node> disconnectCallback, Consumer<Node> tickCallback) {
        updateFrequency();
        NodeRegistry.INSTANCE.connectNodes(this);
        super.internalInitialize(connectCallback, disconnectCallback, tickCallback);
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
