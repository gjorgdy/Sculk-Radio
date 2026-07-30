package nl.gjorgdy.sculk_radio.objects.nodes.teleport;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.ReceiverNode;
import nl.gjorgdy.sculk_radio.utils.VisualUtils;

public class TeleportReceiverNode extends ReceiverNode {

	public static final Codec<TeleportReceiverNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
           BlockPos.CODEC.fieldOf("pos").forGetter(Node::getPos)
	   ).apply(instance, TeleportReceiverNode::new)
	);

	public TeleportReceiverNode(BlockPos pos) {
		super(pos);
	}

	@Override
	public void visualsTick() {
		if (!isLoaded()) return;
		VisualUtils.activateSensor(level, pos);
		VisualUtils.spawnEnderParticles(level, pos, true);
	}

	@Override
	public String toString() {
		return "ReceiverNode{x: " + pos.getX() + ", y: " + pos.getY() + ", z: " + pos.getZ() + "}";
	}

}
