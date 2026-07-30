package nl.gjorgdy.sculk_radio.objects.streams;

import de.maxhenkel.audioplayer.voicechat.VoicechatAudioPlayerPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.packets.MicrophonePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import nl.gjorgdy.sculk_radio.compat.audio_player.MultiLocationalAudioChannel;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.ReceiverNode;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;

import java.util.UUID;

public class MicrophoneStream extends AudioStream {

	private boolean sentPacket = false;
	private final MultiLocationalAudioChannel channel;

	public MicrophoneStream(ServerLevel level, SourceNode<MicrophoneStream> source) {
		VoicechatServerApi api = VoicechatAudioPlayerPlugin.voicechatServerApi;
		if (api == null) {
			throw new IllegalStateException("Simple Voice Chat is not initialized");
		}
		var channelID = UUID.randomUUID();
		var channel = new MultiLocationalAudioChannel(
			channelID,
			"sculkradiomic",
			api.createPosition(source.getPos().getX(), source.getPos().getY(), source.getPos().getZ()),
			() -> {}
		);
		super(
			n -> channel.addChannel(level, new Vec3(n.getPos()).add(0.5)),  // on connect
			n -> channel.removeChannel(new Vec3(n.getPos()).add(0.5)),      // on disconnect
			source,
			true
		);
		this.channel = channel;
	}

	@Override
	public void redstoneTick() {
		var redstoneSignal = getState() == StreamState.ACTIVE ? 15 : 0;
		var analogRedstoneSignal = sentPacket ? 15 : 0;
		if (sentPacket || getState() == StreamState.STOPPED) {
			sentPacket = false;
		}
		if (this.redstoneSignal != redstoneSignal || this.analogRedstoneSignal != analogRedstoneSignal) {
			this.redstoneSignal = redstoneSignal;
			this.analogRedstoneSignal = analogRedstoneSignal;
			forListeners(ReceiverNode::updateNeighbours);
		}
	}

	public void send(MicrophonePacket microphonePacket) {
		if (getState() == StreamState.ACTIVE) {
			channel.send(microphonePacket);
			sentPacket = true;
		}
	}

	@Override
	public void stop() {
		super.stop();
		this.channel.flush();
	}
}
