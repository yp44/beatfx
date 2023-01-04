package org.beatfx.fx.widgets;

import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.beatfx.config.Beat;
import org.beatfx.fx.BeatFXApp;

import java.io.File;

public class BeatPane extends HBox {

    private Label beatID = new Label("#");
    private TextField file = new TextField("Please, select a file");

    private FileChangeListener lastFileChangeListener;

    public BeatPane() {
        super();
        buildUI();
    }

    public void setBeat(Beat beat) {
        if (lastFileChangeListener != null) {
            this.file.textProperty().removeListener(lastFileChangeListener);
        }
        this.beatID.textProperty().setValue(beat.getId().get());
        this.file.setText(beat.getFile().get());
        lastFileChangeListener = new FileChangeListener(beat.getFile());
        this.file.textProperty().addListener(lastFileChangeListener); //.bindBidirectional(beat.getFile());
    }

    private void buildUI() {
        this.setSpacing(BeatFXApp.PADDING);
        file.setMinWidth(300);
        Button selectFile = new Button("...");
        selectFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a MP3 file");
            File f = fileChooser.showOpenDialog(null);
            file.textProperty().setValue(f.getAbsolutePath());
        });
        this.getChildren().addAll(this.beatID, new Label("MP3 file"), file, selectFile);
    }

    private final static class FileChangeListener implements ChangeListener<String> {

        private StringProperty fileProperty;

        public FileChangeListener(StringProperty fileProperty) {
            this.fileProperty = fileProperty;
        }

        @Override
        public void changed(ObservableValue<? extends String> observableValue, String oldFile, String newFile) {
            fileProperty.setValue(newFile);
        }
    }

}
