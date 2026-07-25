package nl.gjorgdy.sculk_radio.objects.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

public class RelayNode extends Node {

	public RelayNode(BlockPos pos) {
		super(pos);
	}

	@Override
	public boolean canTransmit() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	@Override
	public void particleTick(ServerLevel level) {
		ParticleUtils.activateSensor(level, pos);
	}

}
