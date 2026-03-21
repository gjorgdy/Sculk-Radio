package nl.gjorgdy.sculk_radio;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.objects.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class NodeRegistry {

    public static NodeRegistry INSTANCE = new NodeRegistry();

    private final Set<SourceNode> sourceNodes;
    private final Set<RepeaterNode> repeaterNodes;
    private final Set<ReceiverNode> receiverNodes;
    private final Set<CalibratedReceiverNode> calibratedReceiverNodes;

    private NodeRegistry() {
        sourceNodes = new HashSet<>();
        repeaterNodes = new HashSet<>();
        receiverNodes = new HashSet<>();
        calibratedReceiverNodes = new HashSet<>();
    }

    /**
     * Register a source node.
     *
     * @param pos The position of the Sculk Shrieker.
     * @return The node that was registered.
     */
    public SourceNode registerSourceNode(ServerWorld world, BlockPos pos) {
        var node = new SourceNode(world, pos);
        sourceNodes.add(node);
        return node;
    }

    /**
     * Register a receiver node.
     *
     * @param pos The position of the Sculk Sensor.
     * @return The node that was registered.
     */
    public ReceiverNode registerReceiverNode(ServerWorld world, BlockPos pos) {
        var node = new ReceiverNode(world, pos);
        receiverNodes.add(node);
        return node;
    }

    /**
     * Register a calibrated receiver node.
     *
     * @param pos The position of the Calibrated Sculk Sensor.
     * @return The node that was registered.
     */
    public CalibratedReceiverNode registerCalibratedReceiverNode(ServerWorld world, BlockPos pos) {
        var node = new CalibratedReceiverNode(world, pos);
        calibratedReceiverNodes.add(node);
        return node;
    }

    /**
     * Register a repeater node.
     *
     * @param pos The position of the Amethyst Block with Sculk Sensor.
     * @return The node that was registered.
     */
    public RepeaterNode registerRepeaterNode(ServerWorld world, BlockPos pos) {
        var node = new RepeaterNode(world, pos);
        repeaterNodes.add(node);
        return node;
    }

    public void removeNode(Node node) {
        removeNode(node, true);
    }

    public void removeNode(Node node, boolean triggerDisconnect) {
        switch (node) {
            case SourceNode sourceNode -> sourceNodes.remove(sourceNode);
            case RepeaterNode repeaterNode -> repeaterNodes.remove(repeaterNode);
            case CalibratedReceiverNode calibratedReceiverNode ->
                    calibratedReceiverNodes.remove(calibratedReceiverNode);
            case ReceiverNode receiverNode -> receiverNodes.remove(receiverNode);
            default -> {
            }
        }
        if (triggerDisconnect) node.disconnect();
    }

    public void connectNodes(SourceNode sourceNode) {
        if (sourceNode.getFrequency() > 0) {
            for (var receiverNode : calibratedReceiverNodes) {
                if (receiverNode.getFrequency() == sourceNode.getFrequency() && !receiverNode.isConnected()) {
                    boolean connected = sourceNode.connect(receiverNode);
                    if (connected) receiverNode.connect(sourceNode);
                }
            }
        } else {
            internalConnectNodes(sourceNode, 0);
        }
    }

    private void internalConnectNodes(Node node, int depth) {
        if (depth >= 8 || node == null) return;
        // connect to receivers
        for (var rn : getClosestNodes(node, receiverNodes, 8)) {
            boolean connected = node.connect(rn);
            if (connected) rn.connect(node);
        }
        // connect to repeaters
        while (true) {
            var rn = getClosestNode(node, repeaterNodes);
            if (rn == null) break;
            boolean connected = node.connect(rn);
            if (connected) internalConnectNodes(rn, depth + 1);
            else break;
        }
    }

    public <T extends Node> Collection<T> getClosestNodes(Node node, Collection<T> nodes, int count) {
        return nodes.stream()
                .filter(n -> !n.isConnected() && n.getPos().getChebyshevDistance(node.getPos()) < 16 && n != node)
                .sorted(Comparator.comparingInt(a -> a.getPos().getManhattanDistance(node.getPos())))
                .limit(count)
                .collect(Collectors.toSet());
    }

    @Nullable
    public <T extends Node> T getClosestNode(Node node, Collection<T> nodes) {
        return nodes.stream()
                .filter(n -> !n.isConnected() && n.getPos().getChebyshevDistance(node.getPos()) < 16 && n != node)
                .min(Comparator.comparingInt(a -> a.getPos().getManhattanDistance(node.getPos())))
                .orElse(null);
    }

}
