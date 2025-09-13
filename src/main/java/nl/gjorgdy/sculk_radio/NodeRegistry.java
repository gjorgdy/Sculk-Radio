package nl.gjorgdy.sculk_radio;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.sculk_radio.objects.CalibratedReceiverNode;
import nl.gjorgdy.sculk_radio.objects.Node;
import nl.gjorgdy.sculk_radio.objects.ReceiverNode;
import nl.gjorgdy.sculk_radio.objects.SourceNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NodeRegistry {

    public static NodeRegistry INSTANCE = new NodeRegistry();

    private final List<SourceNode> sourceNodes;
    private final List<Node> repeaterNodes;
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

    public void connectNodes(SourceNode sn) {
        if (sn.getFrequency() > 0) {
            for (var rn : calibratedReceiverNodes) {
                if (rn.getFrequency() == sn.getFrequency() && !rn.isPlaying()) {
                    sn.connect(rn);
                }
            }
        } else {
            sn.connect(getClosestReceiver(sn.getPos()));
        }
    }

    public Node getClosestReceiver(BlockPos pos) {
        return receiverNodes.stream()
                .min(Comparator.comparingInt(a -> a.getPos().getManhattanDistance(pos)))
                .orElse(null);
    }

}
