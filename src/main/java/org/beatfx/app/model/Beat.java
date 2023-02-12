package org.beatfx.app.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;
import org.beatfx.app.util.Defaults;

@Data
public class Beat {

    private DoubleProperty angle = new SimpleDoubleProperty(0d);
    private StringProperty id = new SimpleStringProperty("#");
    private StringProperty file = new SimpleStringProperty(Defaults.FILE_PLACEHOLDER);

    public Beat(String id, double angle){
        this.id.setValue(id);
        this.angle.setValue(angle);
    }

}
