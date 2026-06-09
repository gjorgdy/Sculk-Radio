package nl.gjorgdy.sculk_radio.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public abstract class GlobalPos {

	protected final BlockPos pos;
	protected final ServerLevel level;

	public GlobalPos(ServerLevel level, BlockPos pos) {
		this.level = level;
		this.pos = pos;
	}

	public BlockPos getPos() {
		return pos;
	}

	public ServerLevel getLevel() {
		return level;
	}

	protected final boolean inRange(GlobalPos globalPos, int range) {
		return this.getLevel() == globalPos.getLevel() &&
				this.getPos().closerThan(globalPos.getPos(), range);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Node node) {
			return level.equals(node.level) && pos.equals(node.pos);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return level.hashCode() + pos.hashCode();
	}

	@Override
	public String toString() {
		return "[" + pos.getX() + ',' + pos.getY() + ',' + pos.getZ() + ']';
	}

}
