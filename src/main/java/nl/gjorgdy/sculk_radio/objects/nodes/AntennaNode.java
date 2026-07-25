package nl.gjorgdy.sculk_radio.objects.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.ReceiverNode;

public class AntennaNode extends ReceiverNode {

	public AntennaNode(BlockPos pos) {
		super(pos);
	}

	@Override
	public void particleTick(ServerLevel level) {
		// ignore
	}

	@Override
	public boolean canConnect(Node otherNode) {
		if (!(otherNode instanceof RelayNode)) return false;
		// if right under antenna
		var sameX = otherNode.getPos().getX() == this.getPos().getX();
		var sameZ = otherNode.getPos().getZ() == this.getPos().getZ();
		var lowEnough = otherNode.getPos().getY() < (this.getPos().getY() - SculkRadio.minAntennaHeight);
		return sameX && sameZ && lowEnough;
	}
}
