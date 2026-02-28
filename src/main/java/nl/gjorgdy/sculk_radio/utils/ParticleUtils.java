package nl.gjorgdy.sculk_radio.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.BlockPositionSource;
import nl.gjorgdy.sculk_radio.objects.Node;

import static net.minecraft.block.SculkSensorBlock.SCULK_SENSOR_PHASE;

public class ParticleUtils {

    public static void spawnShriekerParticles(Node node) {
        for (int ah = 0; ah < 5; ++ah) {
            node.getWorld().spawnParticles(
                    new ShriekParticleEffect(ah * 5),
                    (double) node.getPos().getX() + 0.5,
                    (double) node.getPos().getY() + 0.5,
                    (double) node.getPos().getZ() + 0.5,
                    1, 0.0, 0.0, 0.0, 0.0
            );
        }
    }

    public static void activateSensor(Node node) {
        var blockstate = node.getWorld().getBlockState(node.getPos());
        if (!blockstate.isOf(Blocks.SCULK_SENSOR) && !blockstate.isOf(Blocks.CALIBRATED_SCULK_SENSOR)) return;
        node.getWorld().getPlayers().forEach(player ->
                player.networkHandler.sendPacket(new BlockUpdateS2CPacket(node.getPos(), blockstate.with(SCULK_SENSOR_PHASE, SculkSensorPhase.ACTIVE)))
        );
    }

    public static void deactivateSensor(Node node) {
        var blockstate = node.getWorld().getBlockState(node.getPos());
        if (!blockstate.isOf(Blocks.SCULK_SENSOR) && !blockstate.isOf(Blocks.CALIBRATED_SCULK_SENSOR)) return;
        node.getWorld().getPlayers().forEach(player ->
                player.networkHandler.sendPacket(new BlockUpdateS2CPacket(node.getPos(), blockstate.with(SCULK_SENSOR_PHASE, SculkSensorPhase.INACTIVE)))
        );
    }

    public static void spawnVibrationParticles(Node from, Node to) {
        if (from.getWorld() != to.getWorld()) return;
        spawnVibrationParticles(from.getWorld(), from.getPos(), to.getPos());
    }

    public static void spawnVibrationParticles(Node to) {
        spawnVibrationParticles(to.getWorld(), to.getPos().up(8), to.getPos());
    }

    private static void spawnVibrationParticles(ServerWorld world, BlockPos from, BlockPos to) {
        world.spawnParticles(new VibrationParticleEffect(
                        new BlockPositionSource(to), 20),
                from.getX() + 0.5, from.getY() + 0.5, from.getZ() + 0.5, 1, 0.0, 0.0, 0.0, 0.0);

        BlockState sensorBlockState = world.getBlockState(to);
        if (sensorBlockState.isOf(Blocks.SCULK_SENSOR) || sensorBlockState.isOf(Blocks.CALIBRATED_SCULK_SENSOR)) {
            world.setBlockState(to, sensorBlockState.with(SCULK_SENSOR_PHASE, SculkSensorPhase.INACTIVE), 3);
            world.scheduleBlockTick(to, sensorBlockState.getBlock(), 20);
        }
    }

    public static void spawnNoteParticles(Node node) {
        Vec3d vec3d = Vec3d.ofBottomCenter(node.getPos()).add(0.0F, 0.7F, 0.0F);
        float f = (float) node.getWorld().getRandom().nextInt(4) / 24.0F;
        node.getWorld().spawnParticles(ParticleTypes.NOTE, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0, f, 0.0F, 0.0F, 1.0F);
    }

}
