package org.beatfx.config;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Data;

@Data
public class BeatConfig {

    /**
     * How many slot should be created ?
     */
    private IntegerProperty nbSlots = new SimpleIntegerProperty(4);

    /**
     * Duration between two slots execution.
     */
    private IntegerProperty slotDuration = new SimpleIntegerProperty(1000);

}
