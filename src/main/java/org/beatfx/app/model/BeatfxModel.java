package org.beatfx.app.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import lombok.Data;

@Data
public class BeatfxModel {

    private ListProperty<Cycle> cycles = new SimpleListProperty<>(FXCollections.observableArrayList(new Cycle("First", false)));

    private ListProperty<PlayerRow> playerRows = new SimpleListProperty<>(FXCollections.observableArrayList(PlayerRow.getFirst(cycles.get(0))));

    public BeatfxModel(){
    }

}
