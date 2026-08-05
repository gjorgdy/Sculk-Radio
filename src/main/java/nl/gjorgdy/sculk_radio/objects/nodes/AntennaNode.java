package nl.gjorgdy.sculk_radio.objects.nodes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CalibratedSculkSensorBlock;
import nl.gjorgdy.sculk_radio.SculkRadio;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.registries.NodeRegistry;
import nl.gjorgdy.sculk_radio.utils.VisualUtils;

import java.util.HashSet;
import java.util.Set;

public class AntennaNode extends RelayNode {

	public static final Codec<AntennaNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
             BlockPos.CODEC.fieldOf("pos").forGetter(Node::getPos),
             Codec.INT.fieldOf("frequency").forGetter(AntennaNode::getFrequency)
         ).apply(instance, AntennaNode::new)
	);

	private int frequency = 0;

	public AntennaNode(BlockPos pos) {
		super(pos);
	}

	protected void internalInit() {
		updateFrequency();
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
		setDirty();
	}

	public void updateFrequency() {
		if (!isLoaded()) return;
		if (frequency > 15 && SculkRadio.tuningEnabled) return; // Return if antenna has a tuned frequency
		var direction = level.getBlockState(getPos()).getValueOrElse(CalibratedSculkSensorBlock.FACING, Direction.UP);
		this.frequency = level.getDirectSignal(
			getPos().relative(direction.getOpposite()),
			direction.getOpposite()
		);
		setDirty();
	}

	private AntennaNode(BlockPos pos, int frequency) {
		super(pos);
		if (!SculkRadio.tuningEnabled && frequency > 15) {
			frequency = 0;
		}
		this.frequency = frequency;
	}

	@Override
	public Set<Node> getNeighbours() {
		Set<Node> neighbours = new HashSet<>();
		neighbours.addAll(this.neighbours);
		neighbours.addAll(NodeRegistry.of(level).getAntennas(frequency));
		return neighbours;
	}

	@Override
	public void pulseNeighbours() {
		getNeighbours().forEach(
			neighbour -> {
				if (neighbour instanceof AntennaNode) {
					VisualUtils.spawnVibrationParticles(level, this.getPos(), this.getPos().above(16));
					VisualUtils.spawnAntennaParticles(level, this.getPos());
				} else {
					VisualUtils.spawnVibrationParticles(level, this.getPos(), neighbour.getPos());
				}
			}
		);
	}

	@Override
	public void visualsTick() {
		if (isLoaded()) VisualUtils.activateSensor(level, this.getPos());
	}

	@Override
	public boolean canConnect(Node otherNode) {
		if (!(otherNode instanceof RelayNode)) return false;
		// if right under antenna
		var sameX = otherNode.getPos().getX() == this.getPos().getX();
		var sameZ = otherNode.getPos().getZ() == this.getPos().getZ();
		var lowEnough = otherNode.getPos().getY() < (this.getPos().getY() - SculkRadio.minAntennaHeight);
		return sameX && sameZ && lowEnough;
	}
}
