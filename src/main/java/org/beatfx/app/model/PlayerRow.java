package org.beatfx.app.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import lombok.Data;

import java.util.stream.Collectors;

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

    public boolean hasCycle(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }

        return cycles.stream().map(c -> c.getId().get()).filter(c -> c.equals(id)).findFirst().isPresent();
    }

    public String getLabel() {
        return this.label.get();
    }

    public StringProperty getLabelProperty() {
        return this.label;
    }

    public String getGotoLabel() {
        return this.gotoLabel.get();
    }

    public StringProperty getGotoLabelProperty() {
        return this.gotoLabel;
    }


    public IntegerProperty getGotoRepeatProperty() {
        return this.gotoRepeat;
    }

    public Integer getGotoRepeat() {
        return this.gotoRepeat.get();
    }

    @Override
    public String toString() {
        return new StringBuilder("PlayerRow[")
                .append(this.getLabel()).append(",")
                .append(this.getGotoLabel()).append(",")
                .append(this.getGotoRepeat()).append(",(")
                .append(this.cycles.stream().map(c -> c.getId().get()).collect(Collectors.joining(" / ")))
                .append(")]")
                .toString();
    }

    public final static PlayerRow getFirst(Cycle first) {
        PlayerRow playerRow = new PlayerRow();
        playerRow.getCycles().add(first);

        return playerRow;
    }

}
