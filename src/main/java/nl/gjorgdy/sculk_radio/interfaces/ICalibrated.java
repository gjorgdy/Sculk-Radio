package nl.gjorgdy.sculk_radio.interfaces;

import nl.gjorgdy.sculk_radio.SculkRadio;

public interface ICalibrated {

    default void onInitialize() {
        SculkRadio.RunIfServerActive(this::updateFrequency);
    }

    int getFrequency();

    void updateFrequency();

}
