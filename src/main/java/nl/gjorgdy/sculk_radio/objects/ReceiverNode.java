package nl.gjorgdy.sculk_radio.objects;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

public class ReceiverNode extends Node {

    public ReceiverNode(ServerLevel world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public void internalTick() {
        ParticleUtils.activateSensor(this);
        ParticleUtils.spawnNoteParticles(this);
    }

    @Override
    public void disconnect() {
        ParticleUtils.deactivateSensor(this);
        super.disconnect();
        getWorld().updateNeighborsAt(getPos().below(), Blocks.NOTE_BLOCK);
    }

    @Override
    public boolean isActive() {
        return isConnected();
    }

    @Override
    public boolean connect(Node toNode) {
        getWorld().updateNeighborsAt(getPos().below(), Blocks.NOTE_BLOCK);
        return false;
    }

}
