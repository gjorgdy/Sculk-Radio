package nl.gjorgdy.sculk_radio.utils;

import net.minecraft.core.TypedInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public abstract class BlockUtils {

	private static TagKey<Block> NOTEBLOCKS_TAG = null;
	private static TagKey<Block> JUKEBOXES_TAG = null;

	static {
		var noteblocksId = Identifier.tryParse("lieonstudio:noteblocks");
		if (noteblocksId != null) {
			NOTEBLOCKS_TAG = TagKey.create(Registries.BLOCK, noteblocksId);
		}
		var jukeboxesId = Identifier.tryParse("lieonstudio:jukeboxes");
		if (jukeboxesId != null) {
			JUKEBOXES_TAG = TagKey.create(Registries.BLOCK, jukeboxesId);
		}
	}

	public static boolean isNoteblock(TypedInstance<Block> blockInstance) {
		return blockInstance.is(Blocks.NOTE_BLOCK) || blockInstance.is(NOTEBLOCKS_TAG);
	}

	public static boolean isJukebox(TypedInstance<Block> blockInstance) {
		return blockInstance.is(Blocks.JUKEBOX) || blockInstance.is(JUKEBOXES_TAG);
	}

}
