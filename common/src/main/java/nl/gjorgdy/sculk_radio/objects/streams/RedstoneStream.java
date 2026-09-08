package nl.gjorgdy.sculk_radio.objects.streams;

import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.ReceiverNode;
import nl.gjorgdy.sculk_radio.objects.nodes.redstone.RedstoneReceiverNode;
import nl.gjorgdy.sculk_radio.objects.nodes.redstone.RedstoneTransmitterNode;

public class RedstoneStream extends Stream {

	public RedstoneStream(RedstoneTransmitterNode source) {
		super(
			n -> n instanceof RedstoneReceiverNode,
			ReceiverNode::updateNeighbours,
			ReceiverNode::updateNeighbours,
			source,
			true
		);
	}

	public void updateListenerNeighbours() {
		forListeners(ReceiverNode::updateNeighbours);
	}

}
