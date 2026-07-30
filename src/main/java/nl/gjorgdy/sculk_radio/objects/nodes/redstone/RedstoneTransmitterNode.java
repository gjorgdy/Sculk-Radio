package nl.gjorgdy.sculk_radio.objects.nodes.redstone;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;
import nl.gjorgdy.sculk_radio.objects.streams.RedstoneStream;
import nl.gjorgdy.sculk_radio.objects.streams.StreamState;

public class RedstoneTransmitterNode extends SourceNode<RedstoneStream> {

	protected int redstoneSignal;

	public static final Codec<RedstoneTransmitterNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
          BlockPos.CODEC.fieldOf("pos").forGetter(Node::getPos),
          Codec.INT.fieldOf("redstone_signal").forGetter(RedstoneTransmitterNode::getRedstoneSignal)
      ).apply(instance, RedstoneTransmitterNode::new)
	);

	private RedstoneTransmitterNode(BlockPos pos, int signal) {
		super(pos);
		this.redstoneSignal = signal;
	}

	public RedstoneTransmitterNode(BlockPos pos) {
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
