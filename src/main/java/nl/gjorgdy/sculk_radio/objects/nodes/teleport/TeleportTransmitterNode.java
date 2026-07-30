package nl.gjorgdy.sculk_radio.objects.nodes.teleport;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;
import nl.gjorgdy.sculk_radio.objects.streams.StreamState;
import nl.gjorgdy.sculk_radio.objects.streams.TeleportStream;
import nl.gjorgdy.sculk_radio.utils.VisualUtils;

public class TeleportTransmitterNode extends SourceNode<TeleportStream> {

	public static final Codec<TeleportTransmitterNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
           BlockPos.CODEC.fieldOf("pos").forGetter(Node::getPos),
           Codec.BOOL.fieldOf("active").forGetter(TeleportTransmitterNode::isActive)
       ).apply(instance, TeleportTransmitterNode::new)
	);

	private boolean active;

	public TeleportTransmitterNode(BlockPos pos) {
		this(pos, false);
	}

	private TeleportTransmitterNode(BlockPos pos, boolean active) {
		super(pos);
		this.active = active;
	}

	@Override
	public void visualsTick() {
		super.visualsTick();
		if (stream != null && stream.getState() == StreamState.ACTIVE) {
			VisualUtils.spawnEnderParticles(level, getPos(), false);
		}
	}

	protected void internalInit() {
		updateState();
	}

	public void updateState() {
		this.active = level.getBestNeighborSignal(getPos().below()) > 0;
		// start if active, stop if not
		if (this.active && getState() != StreamState.ACTIVE) {
			start(_ -> new TeleportStream(this));
			System.out.println("Starting teleport stream at " + getPos());
		} else if (!this.active && getState() == StreamState.ACTIVE) {
			stop();
		}
		setDirty();
	}

	private Boolean isActive() {
		return active;
	}

}
