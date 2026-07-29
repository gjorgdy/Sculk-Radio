package nl.gjorgdy.sculk_radio.objects.nodes.audio;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;
import nl.gjorgdy.sculk_radio.objects.streams.AudioStream;

public class RadioNode extends SourceNode<AudioStream> {

	public static final Codec<RadioNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
             BlockPos.CODEC.fieldOf("pos").forGetter(Node::getPos)
         ).apply(instance, RadioNode::new)
	);

	public RadioNode(BlockPos pos) {
		super(pos);
	}
}
