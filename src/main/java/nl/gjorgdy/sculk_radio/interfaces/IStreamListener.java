package nl.gjorgdy.sculk_radio.interfaces;

import nl.gjorgdy.sculk_radio.connections.SculkStream;
import nl.gjorgdy.sculk_radio.nodes.Node;

public interface IStreamListener {

	void listenTo(Node source, SculkStream stream);

	void stopListening();

	boolean isListening();

	void tick();

}
