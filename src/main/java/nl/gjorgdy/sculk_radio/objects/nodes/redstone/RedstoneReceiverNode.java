package nl.gjorgdy.sculk_radio.objects.nodes.redstone;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.ReceiverNode;
import nl.gjorgdy.sculk_radio.utils.VisualUtils;

public class RedstoneReceiverNode extends ReceiverNode {

	public static final Codec<RedstoneReceiverNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
             BlockPos.CODEC.fieldOf("pos").forGetter(Node::getPos),
             Codec.INT.fieldOf("redstone_signal").forGetter(RedstoneReceiverNode::getOwnSignal)
	     ).apply(instance, RedstoneReceiverNode::new)
	);

	private RedstoneReceiverNode(BlockPos pos, int signal) {
		super(pos);
		this.redstoneSignal = signal;
	}

	public RedstoneReceiverNode(BlockPos pos) {
		super(pos);
	}

	@Override
	public void visualsTick() {
		if (!isLoaded()) return;
		VisualUtils.activateSensor(level, pos);
		VisualUtils.spawnRedstoneParticles(level, pos);
	}

}
