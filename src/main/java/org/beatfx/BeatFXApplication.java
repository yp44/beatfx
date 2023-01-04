package org.beatfx;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.beatfx.config.BeatConfig;


public class BeatFXApplication extends Application {

    private final static double CTRL_SPACE = 10;
    private final static double PADDING = 5;

    private final BeatConfig config = new BeatConfig();

    private final Pane mainePanel = new Pane();

    private Stage stage;

    private int configPanelHeight = 0;

    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;

        HBox configHBox = new HBox();
        configHBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldHeight, Number newHeight) {
                System.out.println(String.format("HBox height has changed from %s to %s.", oldHeight, newHeight));
                configPanelHeight = newHeight.intValue();
            }
        });
        Spinner<Integer> nbSlotCtrl = new Spinner<>(1, 100, config.getNbSlots());
        Spinner<Integer> slotDurationCtrl = new Spinner<>(1, 60000, config.getSlotDuration());

        nbSlotCtrl.getValueFactory().valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer oldInt, Integer newInt) {
                config.setNbSlots(newInt);
            }
        });

        slotDurationCtrl.getValueFactory().valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer oldInt, Integer newInt) {
                config.setSlotDuration(newInt);
            }
        });

        Button okBtn = new Button("Generate");
        okBtn.setOnAction(e -> newPlayer());

        configHBox.setSpacing(CTRL_SPACE);
        configHBox.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        Label nbSlotLbl = new Label("Nb slots");
        Label slotDurationLbl = new Label("Slot duration");

        // Don't do anything !!
        // nbSlotLbl.maxWidth(Double.MAX_VALUE);
        // HBox.setHgrow(nbSlotLbl, Priority.ALWAYS);

        configHBox.setBackground(new Background(new BackgroundFill(new Color(1, 0.5, 0, 1), new CornerRadii(20), new Insets(0, 0, 0, 0))));
        configHBox.getChildren().addAll(nbSlotLbl, nbSlotCtrl, slotDurationLbl, slotDurationCtrl, okBtn);

        VBox mainVBox = new VBox();

        ScrollPane scrollPane = new ScrollPane(mainePanel);
        scrollPane.setBackground(new Background(new BackgroundFill(new Color(0, 0, 1, 1), new CornerRadii(20), new Insets(0, 0, 0, 0))));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mainVBox.getChildren().addAll(configHBox, scrollPane);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        stage.setTitle("BeatFX");
        stage.setScene(new Scene(mainVBox, bounds.getWidth() / 2, bounds.getHeight() / 2));
        stage.show();

    }

    private void newPlayer() {
        System.out.println(String.format("New player: nbSlot(%s), duration(%s)", this.config.getNbSlots(), this.config.getSlotDuration()));

        System.out.println(String.format("Main panel width(%s), height(%s).", mainePanel.getWidth(), mainePanel.getHeight()));

        System.out.printf("Stage(%s, %s)", this.stage.getWidth() , this.stage.getHeight());

        // clean the main pane
        mainePanel.getChildren().clear();
        Circle mainCircle = new Circle((this.stage.getWidth() / 2) - (2 * PADDING) - configPanelHeight, Color.TRANSPARENT); //this.stage.getWidth() / 2, this.stage.getHeight() / 2 , 50);
        mainCircle.setCenterX((this.stage.getWidth() / 2) + PADDING);
        mainCircle.setCenterY((this.stage.getHeight() / 2) + PADDING + (3 * configPanelHeight));
        mainCircle.setStroke(Color.BLACK);
        mainCircle.setStrokeWidth(1);
        mainCircle.getStrokeDashArray().addAll(10.0, 10.0);
        mainePanel.setBackground(new Background(new BackgroundFill(new Color(0.5, 0.5, 0, 1), new CornerRadii(20), new Insets(0, 0, 0, 0))));
        mainePanel.getChildren().add(mainCircle);
    }

    public static void main(String[] args) {
        launch();
    }

}
