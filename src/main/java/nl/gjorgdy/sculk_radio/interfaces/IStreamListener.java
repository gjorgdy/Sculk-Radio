package nl.gjorgdy.sculk_radio.interfaces;

import nl.gjorgdy.sculk_radio.connections.SculkStream;

public interface IStreamListener {

	void listenTo(SculkStream stream);

	boolean isListening();

}
