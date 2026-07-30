package nl.gjorgdy.sculk_radio.objects.streams;

import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.ReceiverNode;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.SpeakerNode;

import java.util.function.Consumer;

public class AudioStream extends Stream {

	public AudioStream(Consumer<? super ReceiverNode> connectConsumer, Consumer<? super ReceiverNode> disconnectConsumer, SourceNode<? extends AudioStream> source, boolean isLive) {
		super(n -> n instanceof SpeakerNode, connectConsumer, disconnectConsumer, source, isLive);
	}

}
