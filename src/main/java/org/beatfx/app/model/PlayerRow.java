package org.beatfx.app.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import lombok.Data;

@Data
public class PlayerRow {

    /**
     * Label for goto loops.
     */
    private StringProperty label = new SimpleStringProperty("");

    /**
     * Label after player this row, go to the one with the given label.
     */
    private StringProperty gotoLabel = new SimpleStringProperty("");

    /**
     * Number of time the goto should be done.
     */
    private IntegerProperty gotoRepeat = new SimpleIntegerProperty(0);

    /**
     * List of cycle to play simultaneously.
     */
    private ListProperty<Cycle> cycles = new SimpleListProperty<>(FXCollections.observableArrayList());


    public final static PlayerRow getFirst(Cycle first){
        PlayerRow playerRow = new PlayerRow();
        playerRow.getCycles().add(first);

        return playerRow;
    }

}
