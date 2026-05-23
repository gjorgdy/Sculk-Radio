package nl.gjorgdy.sculk_radio.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class RelayNode extends Node {

	public RelayNode(ServerLevel world, BlockPos pos) {
		super(world, pos);
	}

	@Override
	public boolean canTransmit() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

}
