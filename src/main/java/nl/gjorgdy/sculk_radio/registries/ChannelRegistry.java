package nl.gjorgdy.sculk_radio.registries;

import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.connections.SculkChannel;
import nl.gjorgdy.sculk_radio.interfaces.IFrequency;

import java.util.HashMap;

public class ChannelRegistry {

	private ServerLevel level;
	private final HashMap<IFrequency, SculkChannel> channels;

	public ChannelRegistry(ServerLevel level) {
		this.level = level;
		this.channels = new HashMap<>();
	}

	public SculkChannel getChannel(IFrequency frequency) {
		return channels.computeIfAbsent(frequency, SculkChannel::new);
	}

}
