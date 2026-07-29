package nl.gjorgdy.sculk_radio.objects.nodes.audio;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.maxhenkel.voicechat.api.packets.MicrophonePacket;
import net.minecraft.core.BlockPos;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;
import nl.gjorgdy.sculk_radio.objects.streams.MicrophoneStream;
import nl.gjorgdy.sculk_radio.objects.streams.StreamState;

public class MicrophoneNode extends SourceNode {

	public static final Codec<MicrophoneNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
           BlockPos.CODEC.fieldOf("pos").forGetter(Node::getPos),
           Codec.BOOL.fieldOf("active").forGetter(MicrophoneNode::isActive)
       ).apply(instance, MicrophoneNode::new)
	);

	private boolean active;

	public MicrophoneNode(BlockPos pos) {
		this(pos, false);
	}

	private MicrophoneNode(BlockPos pos, boolean active) {
		super(pos);
		this.active = active;
	}

	public boolean send(MicrophonePacket microphonePacket) {
		if (getState() == StreamState.ACTIVE && stream instanceof MicrophoneStream microphoneStream) {
			microphoneStream.send(microphonePacket);
			return true;
		}
		return false;
	}

	protected void internalInit() {
		updateState();
	}

	public void updateState() {
		this.active = level.getBestNeighborSignal(getPos().below()) > 0;
		// start if active, stop if not
		if (this.active && getState() != StreamState.ACTIVE) {
			start(_ -> new MicrophoneStream(level, this));
		} else if (!this.active && getState() == StreamState.ACTIVE) {
			stop();
		}
		setDirty();
	}

	private Boolean isActive() {
		return active;
	}

}
