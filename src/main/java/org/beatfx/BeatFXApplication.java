package org.beatfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.beatfx.config.BeatConfig;


public class BeatFXApplication extends Application {

    private BeatConfig config = new BeatConfig();

    @Override
    public void start(Stage stage) throws Exception {

        HBox configHBox = new HBox();
        Spinner<Integer> nbSlotCtrl = new Spinner<>(1, 100, config.getNbSlots().get());
        Spinner<Integer> slotDurationCtrl = new Spinner<>(1, 60000, config.getSlotDuration().get());
        configHBox.getChildren().addAll(new Label("Nb slots"), nbSlotCtrl, new Label("Slot duration"), slotDurationCtrl);

        Canvas canvas = new Canvas();
        StackPane playerPane = new StackPane(canvas);

        VBox mainVBox = new VBox();
        mainVBox.getChildren().addAll(configHBox, playerPane);

        stage.setTitle("BeatFX");
        stage.setScene(new Scene(mainVBox, 320, 240));
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}
