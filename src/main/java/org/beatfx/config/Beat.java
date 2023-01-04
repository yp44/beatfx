package org.beatfx.config;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

@Data
public class Beat {

    private StringProperty id = new SimpleStringProperty();
    private StringProperty file = new SimpleStringProperty();

    public Beat(String id){
        this.id.setValue(id);
    }

}
