package nl.gjorgdy.sculk_radio.registries;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class LevelRegistry {

	private final HashMap<ServerLevel, NodeRegistry> nodeRegistries;

	public LevelRegistry() {
		this.nodeRegistries = new HashMap<>();
	}

	public NodeRegistry getNodeRegistry(@NotNull ServerLevel level) {
		return nodeRegistries.computeIfAbsent(level, _ -> new NodeRegistry());
	}

}
