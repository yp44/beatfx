package org.beatfx.app.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
public class Cycle {

    private boolean deletable = true;

    private StringProperty id = new SimpleStringProperty("");
    private IntegerProperty nbSlots = new SimpleIntegerProperty(8);
    private IntegerProperty duration = new SimpleIntegerProperty(4000);

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<Beat> beats = new ArrayList<>();

    public Cycle(String id){
        this(id, true);
    }

    public Cycle(String id, boolean deletable){
        this.id.setValue(id);
        this.deletable = deletable;
    }

    public void addBeat(Beat beat){
        this.beats.add(beat);
    }

    public void removeBeat(Beat beat){
        this.beats.remove(beat);
    }

    public void clearAllBeats(){
        this.beats.clear();
    }

    public Beat getBeat(int i){
        return this.beats.get(i);
    }

    public List<Beat> getBeats(){
        return new ArrayList<>(this.beats);
    }

}
