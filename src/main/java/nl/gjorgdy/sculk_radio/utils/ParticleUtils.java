package nl.gjorgdy.sculk_radio.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.phys.Vec3;
import nl.gjorgdy.sculk_radio.objects.Node;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.SCULK_SENSOR_PHASE;

public class ParticleUtils {

    public static void spawnShriekerParticles(Node node) {
        for (int ah = 0; ah < 5; ++ah) {
            node.getWorld().sendParticles(
                    new ShriekParticleOption(ah * 5),
                    (double) node.getPos().getX() + 0.5,
                    (double) node.getPos().getY() + 0.5,
                    (double) node.getPos().getZ() + 0.5,
                    1, 0.0, 0.0, 0.0, 0.0
            );
        }
    }

    public static void activateSensor(Node node) {
        var blockstate = node.getWorld().getBlockState(node.getPos());
        if (!blockstate.is(Blocks.SCULK_SENSOR) && !blockstate.is(Blocks.CALIBRATED_SCULK_SENSOR)) return;
        node.getWorld().players().forEach(player ->
                player.connection.send(new ClientboundBlockUpdatePacket(node.getPos(), blockstate.setValue(SCULK_SENSOR_PHASE, SculkSensorPhase.ACTIVE)))
        );
    }

    public static void deactivateSensor(Node node) {
        var blockstate = node.getWorld().getBlockState(node.getPos());
        if (!blockstate.is(Blocks.SCULK_SENSOR) && !blockstate.is(Blocks.CALIBRATED_SCULK_SENSOR)) return;
        node.getWorld().players().forEach(player ->
                player.connection.send(new ClientboundBlockUpdatePacket(node.getPos(), blockstate.setValue(SCULK_SENSOR_PHASE, SculkSensorPhase.INACTIVE)))
        );
    }

    public static void spawnVibrationParticles(Node from, Node to) {
        if (from.getWorld() != to.getWorld()) return;
        spawnVibrationParticles(from.getWorld(), from.getPos(), to.getPos());
    }

    public static void spawnVibrationParticles(Node to) {
        spawnVibrationParticles(to.getWorld(), to.getPos().above(8), to.getPos());
    }

    private static void spawnVibrationParticles(ServerLevel world, BlockPos from, BlockPos to) {
        world.sendParticles(new VibrationParticleOption(
		                             new BlockPositionSource(to), 20),
                from.getX() + 0.5, from.getY() + 0.5, from.getZ() + 0.5, 1, 0.0, 0.0, 0.0, 0.0);

        BlockState sensorBlockState = world.getBlockState(to);
        if (sensorBlockState.is(Blocks.SCULK_SENSOR) || sensorBlockState.is(Blocks.CALIBRATED_SCULK_SENSOR)) {
            world.setBlock(to, sensorBlockState.setValue(SCULK_SENSOR_PHASE, SculkSensorPhase.INACTIVE), 3);
            world.scheduleTick(to, sensorBlockState.getBlock(), 20);
        }
    }

    public static void spawnNoteParticles(Node node) {
        Vec3 vec3 = node.getPos().getBottomCenter().add(0.0F, 0.7F, 0.0F);
        float f = (float) node.getWorld().getRandom().nextInt(4) / 24.0F;
        node.getWorld().sendParticles(ParticleTypes.NOTE, vec3.x, vec3.y, vec3.z, 0, f, 0.0F, 0.0F, 1.0F);
    }

}
