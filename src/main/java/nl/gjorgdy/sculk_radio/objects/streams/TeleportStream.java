package nl.gjorgdy.sculk_radio.objects.streams;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;
import nl.gjorgdy.sculk_radio.objects.nodes.teleport.TeleportReceiverNode;

public class TeleportStream extends Stream {

	private static final byte enderEventId = 46;
	private final AABB teleportBox;

	public TeleportStream(SourceNode<? extends Stream> source) {
		super(
		  n -> n instanceof TeleportReceiverNode,
		  _ -> {},
		  _ -> {},
	      source,
	      true
		);
		this.teleportBox = new AABB(
			source.getPos().getX() - 2,
			source.getPos().getY() - 2,
			source.getPos().getZ() - 2,
			source.getPos().getX() + 2,
			source.getPos().getY() + 2,
			source.getPos().getZ() + 2
		);
	}

	@Override
	public void visualsTick(ServerLevel level) {
		super.visualsTick(level);
		if (getState() == StreamState.ACTIVE) {
			teleportTick(level);
		}
	}

	public void teleportTick(ServerLevel level) {
		level.getEntities(null, this.teleportBox).forEach(entity ->
			forRandomListener(level.getRandom(), listener -> {
				enderEffect(level, entity);
				entity.teleportTo(
					listener.getPos().getX() + 0.5,
					listener.getPos().getY() + 1,
					listener.getPos().getZ() + 0.5
				);
				SculkRadio.scheduleNextTick(() -> enderEffect(level, entity));
			})
		);
	}

	private void enderEffect(ServerLevel level, Entity entity) {
		level.playSound(null, entity.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
		level.broadcastEntityEvent(entity, enderEventId);
	}

}
