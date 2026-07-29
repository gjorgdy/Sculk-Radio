package nl.gjorgdy.sculk_radio.objects.nodes.redstone;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;
import nl.gjorgdy.sculk_radio.objects.streams.RedstoneStream;
import nl.gjorgdy.sculk_radio.objects.streams.StreamState;

public class RedstoneSourceNode extends SourceNode<RedstoneStream> {

	protected int redstoneSignal;

	public static final Codec<RedstoneSourceNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
          BlockPos.CODEC.fieldOf("pos").forGetter(Node::getPos),
          Codec.INT.fieldOf("redstone_signal").forGetter(RedstoneSourceNode::getRedstoneSignal)
      ).apply(instance, RedstoneSourceNode::new)
	);

	private RedstoneSourceNode(BlockPos pos, int signal) {
		super(pos);
		this.redstoneSignal = signal;
	}

	public RedstoneSourceNode(BlockPos pos) {
		this(pos, 0);
	}

	@Override
	protected void internalInit() {
		onSignalChange();
	}

	public void updateRedstone() {
		if (isLoaded()) {
			int newRedstoneSignal = level.getBestNeighborSignal(getPos().below());
			if (newRedstoneSignal != redstoneSignal) {
				redstoneSignal = newRedstoneSignal;
				onSignalChange();
				setDirty();
			}
		}
	}

	private void onSignalChange() {
		if (redstoneSignal > 0) {
			if (stream == null || stream.getState() == StreamState.STOPPED) {
				start(_ -> new RedstoneStream(this));
			}
			stream.sendRedstoneSignal();
		} else {
			if (stream != null && stream.getState() == StreamState.ACTIVE) {
				stop();
			}
		}
	}

	@Override
	public int getRedstoneSignal() {
		return redstoneSignal;
	}
}
