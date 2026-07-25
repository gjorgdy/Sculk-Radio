package nl.gjorgdy.sculk_radio.nodes.audio;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.nodes.Node;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

public class SpeakerNode extends Node {

	public SpeakerNode(BlockPos pos) {
		super(pos);
	}

	@Override
	public boolean canTransmit() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	@Override
	public void particleTick(ServerLevel level) {
		ParticleUtils.activateSensor(level, pos);
		ParticleUtils.spawnNoteParticles(level, pos);
	}

}
