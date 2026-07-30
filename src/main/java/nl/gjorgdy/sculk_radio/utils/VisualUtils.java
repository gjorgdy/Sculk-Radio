package nl.gjorgdy.sculk_radio.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.CommonColors;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.phys.Vec3;
import nl.gjorgdy.sculk_radio.SculkRadio;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.SCULK_SENSOR_PHASE;

public abstract class VisualUtils {

    public static void spawnShriekerParticles(ServerLevel serverLevel, BlockPos pos) {
        for (int ah = 0; ah < 5; ++ah) {
            serverLevel.sendParticles(
                    new ShriekParticleOption(ah * 5),
                    (double) pos.getX() + 0.5,
                    (double) pos.getY() + 0.5,
                    (double) pos.getZ() + 0.5,
                    1, 0.0, 0.0, 0.0, 0.0
            );
        }
    }

    public static void activateSensor(ServerLevel serverLevel, BlockPos pos) {
        var blockstate = serverLevel.getBlockState(pos);
        if (!blockstate.is(Blocks.SCULK_SENSOR) && !blockstate.is(Blocks.CALIBRATED_SCULK_SENSOR)) return;
        serverLevel.players().forEach(player ->
                player.connection.send(new ClientboundBlockUpdatePacket(pos, blockstate.setValue(SCULK_SENSOR_PHASE, SculkSensorPhase.ACTIVE)))
        );
    }

    public static void deactivateSensor(ServerLevel serverLevel, BlockPos pos) {
        var blockstate = serverLevel.getBlockState(pos);
        if (!blockstate.is(Blocks.SCULK_SENSOR) && !blockstate.is(Blocks.CALIBRATED_SCULK_SENSOR)) return;
        serverLevel.players().forEach(player ->
                player.connection.send(new ClientboundBlockUpdatePacket(pos, blockstate.setValue(SCULK_SENSOR_PHASE, SculkSensorPhase.INACTIVE)))
        );
    }

    public static void spawnVibrationParticles(ServerLevel world, BlockPos from, BlockPos to) {
        world.sendParticles(new VibrationParticleOption(
                                    new BlockPositionSource(to), SculkRadio.visualsTick),
                from.getX() + 0.5, from.getY() + 0.5, from.getZ() + 0.5, 1, 0.0, 0.0, 0.0, 0.0);

        BlockState sensorBlockState = world.getBlockState(to);
        if (sensorBlockState.is(Blocks.SCULK_SENSOR) || sensorBlockState.is(Blocks.CALIBRATED_SCULK_SENSOR)) {
            world.setBlock(to, sensorBlockState.setValue(SCULK_SENSOR_PHASE, SculkSensorPhase.INACTIVE), 3);
            world.scheduleTick(to, sensorBlockState.getBlock(), SculkRadio.visualsTick);
        }
    }

    public static void spawnAntennaParticles(ServerLevel world, BlockPos from) {
        world.sendParticles(ParticleTypes.END_ROD, from.getX() + 0.5, from.getY() + 1.0, from.getZ() + 0.5, 5, 0.3, 0.3, 0.3, 0.05);
    }

    public static void spawnNoteParticles(ServerLevel serverLevel, BlockPos pos) {
        Vec3 vec3 = new Vec3(pos).add(0.5F, 0.7F, 0.5F);
        float f = (float) serverLevel.getRandom().nextInt(4) / 24.0F;
        serverLevel.sendParticles(ParticleTypes.NOTE, vec3.x, vec3.y, vec3.z, 0, f, 0.0F, 0.0F, 1.0F);
    }

	public static void spawnRedstoneParticles(ServerLevel level, BlockPos pos) {
        Vec3 vec3 = new Vec3(pos).add(0.5F, 0.7F, 0.5F);
        float f = (float) level.getRandom().nextInt(4) / 24.0F;
        level.sendParticles(new DustParticleOptions(CommonColors.RED, 1f), vec3.x, vec3.y, vec3.z, 0, f, 0.0F, 0.0F, 1.0F);
	}

    public static void spawnEnderParticles(ServerLevel serverLevel, BlockPos pos, boolean reverse) {
        Vec3 vec3 = new Vec3(pos).add(0.5F, 0.5F, 0.5F);
        float f = (float) serverLevel.getRandom().nextInt(4) / 2.0F;
        serverLevel.sendParticles(reverse ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.PORTAL, vec3.x, vec3.y, vec3.z, 64, f, f, f, 0.5F);
    }
}
