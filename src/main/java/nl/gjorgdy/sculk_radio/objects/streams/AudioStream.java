package nl.gjorgdy.sculk_radio.objects.streams;

import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;
import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.SourceNode;
import nl.gjorgdy.sculk_radio.objects.nodes.audio.SpeakerNode;

import java.util.function.Consumer;

public class AudioStream extends Stream {

	public AudioStream(Consumer<? super Node> connectConsumer, Consumer<? super Node> disconnectConsumer, SourceNode<AudioStream> source, boolean isLive) {
		super(n -> n instanceof SpeakerNode, connectConsumer, disconnectConsumer, source, isLive);
	}

}
