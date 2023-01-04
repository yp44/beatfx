package org.beatfx.config;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

@Data
public class Cycle {

    private StringProperty id = new SimpleStringProperty("");
    private IntegerProperty nbSlots = new SimpleIntegerProperty(8);
    private IntegerProperty duration = new SimpleIntegerProperty(4000);
    private IntegerProperty nbLoop = new SimpleIntegerProperty(1);
    private StringProperty nextId = new SimpleStringProperty("");

}
