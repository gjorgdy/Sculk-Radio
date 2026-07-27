package nl.gjorgdy.sculk_radio.objects.nodes.abstracts;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.SculkRadio;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Node {

    protected ServerLevel level;

    private boolean removed = false;
    protected final BlockPos pos;
    protected final Set<Node> neighbours = new HashSet<>();

    public Node(BlockPos pos) {
        this.pos = pos;
    }

    abstract public boolean canTransmit();
    abstract public boolean canReceive();
    abstract public void visualsTick();

    public void init(ServerLevel level) {
        this.level = level;
    }

    public boolean isLoaded() {
        return level != null && level.isLoaded(pos);
    }

    /**
     * @return All connected nodes
     */
    public Set<Node> getNeighbours() {
        return Collections.unmodifiableSet(neighbours);
    }

    public BlockPos getPos() {
        return pos;
    }

    public boolean canConnect(Node otherNode) {
        return ((this.canTransmit() && otherNode.canReceive()) || (otherNode.canTransmit() && this.canReceive()))
            && otherNode.pos.distChessboard(this.pos) <= SculkRadio.maxNodeRange;
    }

    /**
     * Connect a node as neighbour
     *
     * @param otherNode another node
     */
    public final void connect(Node otherNode) {
        this.neighbours.add(otherNode);
        otherNode.neighbours.add(this);
    }

    /**
     * If this node was removed
     */
    public final boolean wasRemoved() {
        return removed;
    }

    /**
     * Called after this node gets removed from the world
     */
	public void afterRemove() {
        neighbours.forEach(otherNode -> otherNode.neighbours.remove(this));
        neighbours.clear();
        removed = true;
	}

    @Override
    public String toString() {
        return "Node{x: " + pos.getX() + ", y: " + pos.getY() + ", z: " + pos.getZ() + "}";
    }

    public ServerLevel getLevel() {
        return level;
    }
}
