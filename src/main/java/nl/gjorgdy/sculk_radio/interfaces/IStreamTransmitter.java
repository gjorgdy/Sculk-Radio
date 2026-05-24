package nl.gjorgdy.sculk_radio.interfaces;

import nl.gjorgdy.sculk_radio.connections.SculkStream;
import org.jspecify.annotations.Nullable;

public interface IStreamTransmitter {

	@Nullable SculkStream getStream();

}
