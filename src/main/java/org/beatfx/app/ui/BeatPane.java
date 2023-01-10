package org.beatfx.app.ui;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.beatfx.app.model.Beat;
import org.beatfx.app.util.Defaults;

import java.io.File;

public class BeatPane extends HBox {

    private TextField beatID = new TextField("#");
    private TextField file = new TextField("");

    private Beat lastBeat = null;


    public BeatPane() {
        super();
        buildUI();
    }

    public void setBeat(Beat beat) {
        if(lastBeat != null){
            this.beatID.textProperty().unbindBidirectional(lastBeat.getId());
            this.file.textProperty().unbindBidirectional(lastBeat.getFile());
        }

        this.beatID.textProperty().bindBidirectional(beat.getId());
        this.file.textProperty().bindBidirectional(beat.getFile());

        this.lastBeat = beat;
    }

    private void buildUI() {
        this.setSpacing(Defaults.PADDING);
        file.setMinWidth(300);
        Button selectFile = new Button("...");
        selectFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a MP3 file");
            File f = fileChooser.showOpenDialog(null);
            file.textProperty().setValue(f.getAbsolutePath());
        });
        this.getChildren().addAll(this.beatID, new Label("Sample file"), file, selectFile);
    }

}
