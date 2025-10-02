package nl.gjorgdy.sculk_radio;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.objects.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NodeRegistry {

    public static NodeRegistry INSTANCE = new NodeRegistry();

    private final List<SourceNode> sourceNodes;
    private final List<RepeaterNode> repeaterNodes;
    private final List<ReceiverNode> receiverNodes;
    private final List<CalibratedReceiverNode> calibratedReceiverNodes;

    private NodeRegistry() {
        sourceNodes = new ArrayList<>();
        repeaterNodes = new ArrayList<>();
        receiverNodes = new ArrayList<>();
        calibratedReceiverNodes = new ArrayList<>();
    }

    /**
     * Register a source node.
     *
     * @param pos The position of the sculk shrieker.
     * @return The node that was registered.
     */
    public SourceNode registerSourceNode(ServerWorld world, BlockPos pos) {
        var node = new SourceNode(world, pos);
        sourceNodes.add(node);
        return node;
    }

    public ReceiverNode registerReceiverNode(ServerWorld world, BlockPos pos) {
        var node = new ReceiverNode(world, pos);
        receiverNodes.add(node);
        return node;
    }

    public CalibratedReceiverNode registerCalibratedReceiverNode(ServerWorld world, BlockPos pos) {
        var node = new CalibratedReceiverNode(world, pos);
        calibratedReceiverNodes.add(node);
        return node;
    }

    public RepeaterNode registerRepeaterNode(ServerWorld world, BlockPos pos) {
        var node = new RepeaterNode(world, pos);
        repeaterNodes.add(node);
        return node;
    }

    public void removeNode(Node node) {
        System.out.println("removed node at " + node.getPos());
        switch (node) {
            case SourceNode sourceNode -> sourceNodes.remove(sourceNode);
            case RepeaterNode repeaterNode -> repeaterNodes.remove(repeaterNode);
            case CalibratedReceiverNode calibratedReceiverNode ->
                    calibratedReceiverNodes.remove(calibratedReceiverNode);
            case ReceiverNode receiverNode -> receiverNodes.remove(receiverNode);
            default -> {
            }
        }
        node.disconnect();
    }

    public void connectNodes(SourceNode sn) {
        if (sn.getFrequency() > 0) {
            for (var rn : calibratedReceiverNodes) {
                if (rn.getFrequency() == sn.getFrequency() && !rn.isConnected()) {
                    sn.connect(rn);
                }
            }
        } else {
            internalConnectNodes(sn, 0);
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
