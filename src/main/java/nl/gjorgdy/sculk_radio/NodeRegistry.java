package nl.gjorgdy.sculk_radio;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.enums.NodeTypes;
import nl.gjorgdy.sculk_radio.objects.Node;
import nl.gjorgdy.sculk_radio.objects.ReceiverNode;
import nl.gjorgdy.sculk_radio.objects.SourceNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NodeRegistry {

    public static NodeRegistry INSTANCE = new NodeRegistry();

    private final List<Node> sourceNodes;
    private final List<Node> repeaterNodes;
    private final List<Node> receiverNodes;

    private NodeRegistry() {
        sourceNodes = new ArrayList<>();
        repeaterNodes = new ArrayList<>();
        receiverNodes = new ArrayList<>();
    }

    /**
     * Register a source node.
     * @param pos The position of the jukebox block.
     * @return The node that was registered.
     */
    public Node registerSourceNode(ServerWorld world, BlockPos pos, NodeTypes type) {
        Node node;
        System.out.println("Registered node at " + pos + " with type " + type + ".");
        switch (type) {
            case SOURCE:
                node = new SourceNode(pos, world);
                sourceNodes.add(node);
                return node;
            case REPEATER:
                node = new ReceiverNode(pos, world);
                repeaterNodes.add(node);
                return node;
            case RECEIVER:
                node = new ReceiverNode(pos, world);
                receiverNodes.add(node);
                return node;
            default:
                return null;
        }
    }

    public List<Node> connectNodes(SourceNode sourceNode) {
        if (sourceNode.getFrequency() > 0) {
            return receiverNodes.stream()
                .filter(n -> {
                    if (n instanceof ReceiverNode rn) {
                        return rn.getFrequency() == sourceNode.getFrequency();
                    }
                    return false;
                })
                .toList();
        }
        return List.of(getClosestReceiver(sourceNode.getPos()));
    }

    public Node getClosestReceiver(BlockPos pos) {
        return receiverNodes.stream()
            .min(Comparator.comparingInt(a -> a.getPos().getManhattanDistance(pos)))
            .orElse(null);
    }

}
