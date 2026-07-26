package nl.gjorgdy.sculk_radio.objects.nodes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.utils.ParticleUtils;

public class RelayNode extends Node {

	public static final Codec<RelayNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
               BlockPos.CODEC.fieldOf("pos").forGetter(Node::getPos)
       ).apply(instance, RelayNode::new)
	);

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
