package nl.gjorgdy.sculk_radio.objects.streams;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.compat.audio_player.MultiLocationalAudioChannel;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;

public class AudioPlayerDiscStream extends AudioStream {

	private final MultiLocationalAudioChannel channel;

	public AudioPlayerDiscStream(MultiLocationalAudioChannel channel, ServerLevel level, SourceNode<AudioStream> source) {
		super(
			n -> channel.addChannel(level, new Vec3(n.getPos()).add(0.5)),  // on connect
			n -> channel.removeChannel(new Vec3(n.getPos()).add(0.5)),      // on disconnect
			source,
			true
		);
		this.channel = channel;
	}

	@Override
	public void connectionTick() {
		super.connectionTick();
		if (SculkRadio.forceSync) channel.sync();
	}
}
