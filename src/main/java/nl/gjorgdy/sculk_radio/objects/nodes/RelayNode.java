package nl.gjorgdy.sculk_radio.objects.nodes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.utils.VisualUtils;

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
	public void visualsTick() {
		if (isLoaded()) VisualUtils.activateSensor(level, pos);
	}

	@Override
	public boolean canConnect(Node otherNode) {
		return otherNode instanceof AntennaNode || super.canConnect(otherNode);
	}
}
