package nl.gjorgdy.sculk_radio.registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.utils.SetUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FrequencyRegistry extends SavedData {

	private static final int lower = 0x111111;
	private static final int upper = 0xFFFFFF;

	private static final Map<ServerLevel, FrequencyRegistry> registryMap = new HashMap<>();
	private final ServerLevel level;
	private final Set<Integer> frequencies;

	public static FrequencyRegistry of(ServerLevel level) {
		return registryMap.computeIfAbsent(level, FrequencyRegistry::load);
	}

	public int getNewFrequency() {
		int newFrequency = randomFrequency();
		while (frequencies.contains(newFrequency)) {
			newFrequency = randomFrequency();
		}
		frequencies.add(newFrequency);
		setDirty();
		return newFrequency;
	}

	private int randomFrequency() {
		return level.getRandom().nextInt(lower, upper);
	}

	private static FrequencyRegistry load(ServerLevel level) {
		Codec<FrequencyRegistry> codec = RecordCodecBuilder.create(instance -> instance.group(
		      Codec.INT.listOf().fieldOf("frequencies").forGetter(i -> SetUtils.toList(i.frequencies))
		).apply(instance, (frequencies) -> new FrequencyRegistry(level, SetUtils.toSet(frequencies))));
		//noinspection DataFlowIssue
		var type = new SavedDataType<>(
			Identifier.fromNamespaceAndPath(SculkRadio.MOD_ID, "frequencies"),
			() -> new FrequencyRegistry(level),
			codec,
			null
		);
		return level.getDataStorage().computeIfAbsent(type);
	}

	private FrequencyRegistry(ServerLevel level) {
		this.level = level;
		this.frequencies = new HashSet<>();
	}

	private FrequencyRegistry(ServerLevel level, Set<Integer> frequencies) {
		this.level = level;
		this.frequencies = frequencies;
	}

}
