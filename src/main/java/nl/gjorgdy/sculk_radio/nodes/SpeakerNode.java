package nl.gjorgdy.sculk_radio.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.connections.SculkStream;

public class SpeakerNode extends SculkStream.StreamConsumerNode {

	public SpeakerNode(ServerLevel level, BlockPos pos) {
		super(level, pos);
	}

	@Override
	public boolean canTransmit() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

}
