package nl.gjorgdy.sculk_radio.registries;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class LevelRegistry {

	private final HashMap<ServerLevel, NodeRegistry> nodeRegistries;
	private final HashMap<ServerLevel, ChannelRegistry> channelRegistries;

	public LevelRegistry() {
		this.nodeRegistries = new HashMap<>();
		this.channelRegistries = new HashMap<>();
	}

	public NodeRegistry getNodeRegistry(@NotNull ServerLevel level) {
		return nodeRegistries.computeIfAbsent(level, NodeRegistry::new);
	}

	public ChannelRegistry getChannelRegistry(@NotNull ServerLevel level) {
		return channelRegistries.computeIfAbsent(level, ChannelRegistry::new);
	}

}
