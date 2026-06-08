package nl.gjorgdy.sculk_radio.connections;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import nl.gjorgdy.sculk_radio.interfaces.IFrequency;
import nl.gjorgdy.sculk_radio.nodes.Node;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SculkChannel {

	private final Set<ChannelNode> antennas;

	private final IFrequency frequency;

	public SculkChannel(IFrequency frequency) {
		this.frequency = frequency;
		this.antennas = new HashSet<>();
	}

	public Set<SculkStream> getStreams() {
		var streams = new HashSet<SculkStream>();
		for (var antenna : antennas) {
			streams.addAll(antenna.getCluster().getStreams());
		}
		return streams;
	}

	public void forNetworks(Consumer<? super SculkCluster> action) {
		antennas.forEach(node -> action.accept(node.getCluster()));
	}

	public void connect(ChannelNode antenna) {
		if (!frequency.equals(antenna.frequency)) {
			return;
		}
		antenna.channel = this;
		antennas.add(antenna);
	}

	public void disconnect(ChannelNode antenna) {
		antenna.channel = null;
		antennas.remove(antenna);
	}

	public static abstract class ChannelNode extends Node {

		@Nullable
		private SculkChannel channel;

		private IFrequency frequency;

		public ChannelNode(ServerLevel level, BlockPos pos) {
			super(level, pos);
		}

		final public @Nullable SculkChannel getChannel() {
			return channel;
		}

		final public void setFrequency(@Nullable IFrequency frequency) {
			if (channel != null) {
				channel.disconnect(this);
			}
			this.frequency = frequency;
			if (frequency == null) return;
			// TODO: get and connect to channel matching frequency
		}

		@Override
		public void afterRemove() {
			super.afterRemove();
			setFrequency(null);
		}

		abstract public void updateFrequency();
	}

}
