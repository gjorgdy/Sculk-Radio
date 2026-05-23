package nl.gjorgdy.sculk_radio.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.connections.SculkCluster;

public abstract class Node extends SculkCluster.ClusterNode {

    private final BlockPos pos;
    private final ServerLevel level;

    public Node(ServerLevel level, BlockPos pos) {
	    this.level = level;
        this.pos = pos;
        super(level);
    }

    public BlockPos getPos() {
        return pos;
    }

    public ServerLevel getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Node node) {
            return level.equals(node.level) && pos.equals(node.pos);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return level.hashCode() + pos.hashCode();
    }

    @Override
    protected boolean innerCanConnect(SculkCluster.ClusterNode otherNode) {
        return otherNode instanceof Node otherPositionNode && innerCanConnect(otherPositionNode);
    }

    protected boolean innerCanConnect(Node otherNode) {
        var inRange = this.getPos().closerThan(otherNode.getPos(), SculkRadio.innerClusterRange);
        return super.innerCanConnect(otherNode) && inRange;
    }

    @Override
    public String toString() {
        return "[" + pos.getX() + ',' + pos.getY() + ',' + pos.getZ() + ']';
    }
}
