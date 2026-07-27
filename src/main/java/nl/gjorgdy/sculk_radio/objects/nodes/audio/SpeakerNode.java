package nl.gjorgdy.sculk_radio.objects.nodes.audio;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.ReceiverNode;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

public class SpeakerNode extends ReceiverNode {

	public static final Codec<SpeakerNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
           BlockPos.CODEC.fieldOf("pos").forGetter(Node::getPos)
	   ).apply(instance, SpeakerNode::new)
	);

	public SpeakerNode(BlockPos pos) {
		super(pos);
	}

	@Override
	public void particleTick(ServerLevel level) {
		ParticleUtils.activateSensor(level, pos);
		ParticleUtils.spawnNoteParticles(level, pos);
	}

	@Override
	public String toString() {
		return "SpeakerNode{x: " + pos.getX() + ", y: " + pos.getY() + ", z: " + pos.getZ() + "}";
	}

}
