package nl.gjorgdy.sculk_radio.objects.streams;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.LevelAccessor;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;

public class VanillaDiscStream extends AudioStream {
	
	public VanillaDiscStream(LevelAccessor level, Holder<JukeboxSong> song, SourceNode source) {
		int songId = level.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG).getId(song.value());
		super(
			n -> level.levelEvent(1010, n.getPos(), songId), // connect
			n -> level.levelEvent(1011, n.getPos(), 0), // disconnect
			source,
			false
		);
	}

}
