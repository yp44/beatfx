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
    private Integer nbSlots = 4;

    /**
     * Duration between two slots execution.
     */
    private Integer slotDuration = 1000;

}
