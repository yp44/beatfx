package org.beatfx.app.ui;

import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class EditorLine extends HBox {

    public EditorLine(){
        super();

        buildPane();
    }

    private void buildPane(){
        TextField label = new TextField("");
        TextField gotoLbl = new TextField("");
        Spinner<Number> gotoRepeat = new Spinner<>();

        this.getChildren().addAll(label, gotoLbl, gotoRepeat);
    }

}
