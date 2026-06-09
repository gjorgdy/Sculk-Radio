package nl.gjorgdy.sculk_radio.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.connections.SculkStream;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Node extends GlobalPos {

    /**
     * All nodes that are connected to this one.
     */
    protected final Set<Node> neighbours = new HashSet<>();
    /**
     * All streams this node receives and/or transmits
     * -
     * Key: the stream itself, Value: the source of the stream
     */
    protected final Map<SculkStream, Node> streams = new HashMap<>();

    public Node(ServerLevel level, BlockPos pos) {
        super(level, pos);
    }

    abstract public boolean canTransmit();
    abstract public boolean canReceive();

    public boolean canConnect(Node otherNode) {
        return ((this.canTransmit() && otherNode.canReceive()) || (otherNode.canTransmit() && this.canReceive()))
            && inRange(otherNode, SculkRadio.innerClusterRange);
    }

    public Node receivesStreamFrom(SculkStream stream) {
        return streams.get(stream);
    }

    /**
     * Connect both nodes to each other
     *
     * @param otherNode another node
     */
    public final void connect(Node otherNode) {
        System.out.println("Connecting " + this + " to " + otherNode);
        if (otherNode == this) return;
        if (!canConnect(otherNode) || !otherNode.canConnect(this)) return;
        var thisToOther = neighbours.add(otherNode);
        if (thisToOther) this.onConnect(otherNode);
        var otherToThis = otherNode.neighbours.add(this);
        if (otherToThis) otherNode.onConnect(this);
    }

    /**
     * Called when a new node gets connected
     * @param connectedNode the node which connected to this
     */
    protected void onConnect(Node connectedNode) {
        if (!this.canReceive() || !connectedNode.canTransmit()) return;
        for (var stream : connectedNode.streams.keySet()) {
            if (streams.containsKey(stream)) continue;
            if (streams.put(stream, connectedNode) == null) onReceive(stream);
        }
    }

    /**
     * Called when this node receives a stream
     * @param receivedStream the stream that was received by this node
     */
    protected void onReceive(SculkStream receivedStream) {
        announceStream(receivedStream);
    }

    /**
     * Disconnect both nodes from each other
     *
     * @param otherNode another node
     */
    public final void disconnect(Node otherNode) {
        var thisToOther = neighbours.remove(otherNode);
        var otherToThis = otherNode.neighbours.remove(this);
        if (otherToThis) otherNode.onDisconnect(this);
    }

    /**
     * Called when a new node gets disconnected
     * @param connectedNode the node which disconnected from this
     */
    protected void onDisconnect(Node connectedNode) {
        if (!this.canReceive()) return;
        for (var streamEntry : streams.entrySet()) {
            if (streamEntry.getValue() != connectedNode) continue;

            var oldStream = streamEntry.getKey();
            if (!reconnectStream(oldStream)) {
                streams.remove(oldStream);
                onStopReceive(oldStream);
            }
        }
    }

    protected void announceStream(SculkStream stream) {
        if (!canTransmit()) return;
        for (var neighbour : neighbours) {
            if (!neighbour.canReceive()) continue;
            if (!neighbour.streams.containsKey(stream) && neighbour.streams.put(stream, this) == null) {
                neighbour.onReceive(stream);
            }
        }
    }

    /**
     * Try to connect to a stream from a neighbor
     * @param stream the stream to find
     */
    protected boolean reconnectStream(SculkStream stream) {
        for (var neighbor : neighbours) {
            if (neighbor.streams.containsKey(stream) && neighbor.streams.get(stream) != this) {
                streams.put(stream, neighbor);
                return true;
            }
        }
        return false;
    }

    /**
     * Called when this node stops receiving a stream
     * @param receivedStream the stream that removed from this node
     */
    protected void onStopReceive(SculkStream receivedStream) {
        for (var neighbor : neighbours) {
            if (neighbor.streams.get(receivedStream) == this) {
                neighbor.streams.remove(receivedStream);
                neighbor.onStopReceive(receivedStream);
            }
        }
    }

    /**
     * Called after this node gets removed from the world
     */
	public void afterRemove() {
        streams.clear();
        var oldNeighbors = new HashSet<>(neighbours);
        oldNeighbors.forEach(this::disconnect);
	}
}
