package org.beatfx.app.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import lombok.Data;

import java.util.Optional;

@Data
public class BeatfxModel {

    private ListProperty<Cycle> cycles = new SimpleListProperty<>(FXCollections.observableArrayList(new Cycle("First", false)));

    private ListProperty<PlayerRow> playerRows = new SimpleListProperty<>(FXCollections.observableArrayList(PlayerRow.getFirst(cycles.get(0))));

    public BeatfxModel(){
    }

    public Optional<Cycle> getCycleById(String id){
        if(id == null || id.isEmpty()){
            return Optional.empty();
        }
        return cycles.stream().filter(c -> c.getId().get().equals(id)).findFirst();
    }

}
