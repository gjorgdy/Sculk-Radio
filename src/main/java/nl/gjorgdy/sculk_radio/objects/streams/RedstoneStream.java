package nl.gjorgdy.sculk_radio.objects.streams;

import nl.gjorgdy.sculk_radio.objects.nodes.redstone.RedstoneReceiverNode;
import nl.gjorgdy.sculk_radio.objects.nodes.redstone.RedstoneTransmitterNode;

public class RedstoneStream extends Stream {

	public RedstoneStream(RedstoneTransmitterNode source) {
		super(
			n -> n instanceof RedstoneReceiverNode,
			n -> n.setRedstoneSignal(source.getRedstoneSignal()),
			n -> n.setRedstoneSignal(0),
			source,
			true
		);
	}

	public void sendRedstoneSignal() {
		forListeners(n -> n.setRedstoneSignal(source.getRedstoneSignal()));
	}

	@Override
	public void redstoneTick() {
		// ignore
	}
}
