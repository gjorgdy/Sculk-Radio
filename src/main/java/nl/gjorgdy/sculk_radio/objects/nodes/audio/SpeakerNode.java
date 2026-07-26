package nl.gjorgdy.sculk_radio.objects.nodes.audio;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.ReceiverNode;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

import java.util.Optional;

public class SpeakerNode extends ReceiverNode {

	public static final Codec<SpeakerNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
           BlockPos.CODEC.fieldOf("pos").forGetter(Node::getPos),
           Codec.INT.optionalFieldOf("weakRedstonePower").forGetter(SpeakerNode::getWeakRedstonePower),
           Codec.INT.optionalFieldOf("strongRedstonePower").forGetter(SpeakerNode::getStrongRedstonePower)
	   ).apply(instance, SpeakerNode::new)
	);

	public SpeakerNode(BlockPos pos) {
		super(pos);
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType") // required for codec
	private SpeakerNode(BlockPos pos, Optional<Integer> weakRedstonePower, Optional<Integer> strongRedstonePower) {
		super(pos, weakRedstonePower.orElse(0), strongRedstonePower.orElse(0));
	}

	@Override
	public void particleTick(ServerLevel level) {
		ParticleUtils.activateSensor(level, pos);
		ParticleUtils.spawnNoteParticles(level, pos);
	}

}
