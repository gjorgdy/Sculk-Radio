package nl.gjorgdy.sculk_radio.objects.streams;

import de.maxhenkel.audioplayer.voicechat.VoicechatAudioPlayerPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.packets.MicrophonePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.compat.audio_player.MultiLocationalAudioChannel;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.SpeakerNode;

import java.util.UUID;

public class MicrophoneStream extends Stream {

	private boolean sentPacket = false;
	private final MultiLocationalAudioChannel channel;

	public MicrophoneStream(ServerLevel level, SourceNode source) {
		VoicechatServerApi api = VoicechatAudioPlayerPlugin.voicechatServerApi;
		if (api == null) {
			throw new IllegalStateException("Simple Voice Chat is not initialized");
		}
		var channelID = UUID.randomUUID();
		var channel = new MultiLocationalAudioChannel(
			channelID,
			api.createPosition(source.getPos().getX(), source.getPos().getY(), source.getPos().getZ()),
			() -> {}
		);
		channel.setDistance(SculkRadio.speakerRange);
		channel.setCategory("microphones");
		channel.setFilter(null);
		super(
			n -> n instanceof SpeakerNode,
			n -> channel.addChannel(level, new Vec3(n.getPos()).add(0.5)),  // on connect
			n -> channel.removeChannel(new Vec3(n.getPos()).add(0.5)),      // on disconnect
			source,
			true
		);
		this.channel = channel;
	}

	@Override
	public void redstoneTick() {
		int signal = getState() == StreamState.ACTIVE ? 15 : 0;
		int analogSignal = sentPacket ? 15 : 0;
		forListeners(listener -> {
			listener.setRedstoneSignal(signal);
			listener.setAnalogRedstoneSignal(analogSignal);
		});
		if (sentPacket || getState() == StreamState.STOPPED) {
			sentPacket = false;
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
