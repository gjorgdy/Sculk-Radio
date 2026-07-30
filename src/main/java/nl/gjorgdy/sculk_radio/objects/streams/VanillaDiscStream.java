package nl.gjorgdy.sculk_radio.objects.streams;

import net.minecraft.world.level.LevelAccessor;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;

public class VanillaDiscStream extends AudioStream {
	
	public VanillaDiscStream(LevelAccessor level, int songId, SourceNode<AudioStream> source) {
		super(
			n -> level.levelEvent(1010, n.getPos(), songId), // connect
			n -> level.levelEvent(1011, n.getPos(), 0), // disconnect
			source,
			false
		);
	}

}
