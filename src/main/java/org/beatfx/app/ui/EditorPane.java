package org.beatfx.app.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.beatfx.app.model.BeatfxModel;
import org.beatfx.app.model.Cycle;

public class EditorPane extends ScrollPane {

    private BeatfxModel beatfxModel;

    public EditorPane(BeatfxModel beatfxModel) {
        super();
        this.beatfxModel = beatfxModel;
        this.setMinHeight(75);

        beatfxModel.getCycles().addListener(new ChangeListener<ObservableList<Cycle>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<Cycle>> observableValue, ObservableList<Cycle> oldList, ObservableList<Cycle> newList) {
                System.out.println("Change cycles list, size : " + newList.size());
                EditorPane.this.refreshEditor();
            }
        });
    }

    private void refreshEditor() {
        System.out.println("Refresh editor....");
        VBox editor = new VBox();

        HBox header = new HBox();
        header.getChildren().addAll(new Label("Label"), new Label("Goto"), new Label("Repeat goto"));
        this.beatfxModel.getCycles().stream().forEach(c -> {
            Label cycleHeader = new Label(c.getId().get());
            header.getChildren().add(cycleHeader);
            cycleHeader.textProperty().bindBidirectional(c.getId());
        });

        EditorLine firstLine = new EditorLine();

        HBox menu = new HBox();
        Button add = new Button("+");

        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Click !!!");
                EditorLine editorLine = new EditorLine();
                editor.getChildren().add(editorLine.getChildren().size() - 1,editorLine);
            }
        });

        menu.getChildren().add(add);

        editor.getChildren().addAll(header, firstLine, menu);

        this.setContent(editor);

    }

}
