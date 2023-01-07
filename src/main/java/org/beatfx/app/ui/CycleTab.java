package org.beatfx.app.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.beatfx.app.model.Beat;
import org.beatfx.app.model.Cycle;
import org.beatfx.app.util.Defaults;

import java.util.ArrayList;
import java.util.List;

public class CycleTab extends Tab {

    private final static int BEAT_CIRCLE_RADIUS = 40;


    private HBox menuPane;

    private Pane cyclePane;

    private BeatPane beatConfPane;

    private Circle mainCircle;

    private ImageView needle;

    private final List<Circle> beatCircles = new ArrayList<>();

    private final Cycle cycle;

    private double lastStageHeight = 0;

    public CycleTab(Cycle cycle) {
        super(cycle.getId().get());
        this.textProperty().bindBidirectional(cycle.getId());
        this.cycle = cycle;
        buildUI();
    }


    public void stageResized(Stage stage){
        redrawCycle(stage);
    }

    private void buildUI() {
        this.textProperty().bindBidirectional(this.cycle.getId());

        this.menuPane = buildMenuPane();
        buildCyclePane();
        this.beatConfPane = new BeatPane();

        VBox splitPane = new VBox();
        splitPane.getChildren().addAll(this.cyclePane, this.beatConfPane);

        VBox mainPane = new VBox();
        mainPane.getChildren().addAll(menuPane, splitPane);

        this.setClosable(this.cycle.isDeletable());

        this.setContent(mainPane);
    }

    private HBox buildMenuPane() {
        HBox menu = new HBox();
        menu.setPadding(new Insets(Defaults.PADDING, Defaults.PADDING, Defaults.PADDING, Defaults.PADDING));
        menu.setSpacing(Defaults.PADDING);

        TextField idCtrl = new TextField();
        idCtrl.textProperty().bindBidirectional(this.cycle.getId());

        Spinner<Number> nbSlotsCtrl = new Spinner<>(1, 100, cycle.getNbSlots().get());
        nbSlotsCtrl.editableProperty().setValue(true);
        nbSlotsCtrl.getValueFactory().valueProperty().bindBidirectional(this.cycle.getNbSlots());

        Spinner<Number> durationCtrl = new Spinner<>(1, 3600000, cycle.getDuration().get());
        durationCtrl.editableProperty().setValue(true);
        durationCtrl.getValueFactory().valueProperty().bindBidirectional(cycle.getDuration());

        Button refreshBtn = new Button("Refresh");

        refreshBtn.setOnAction(e -> {
            buildCyclePane();
            redrawCycle();
        });

        menu.getChildren().addAll(
                new Label("Id"),
                idCtrl,
                new Label("Number of slots"),
                nbSlotsCtrl,
                new Label("Duration"),
                durationCtrl,
                refreshBtn);

        return menu;
    }

    private void buildCyclePane() {
        if (this.cyclePane != null) {
            System.out.println("CLEAR PANEL!!!");
            this.cyclePane.getChildren().clear();
        } else {
            this.cyclePane = new Pane();
        }


        //cycle.setBackground(new Background(new BackgroundFill(new Color(0.7, 0.5, 0.5, 1), new CornerRadii(0), new Insets(0, 0, 0, 0))));
        this.cyclePane.setMaxHeight(Double.MAX_VALUE);

        mainCircle = new Circle(100, Color.TRANSPARENT);
        mainCircle.setCenterX(this.cyclePane.getWidth() / 2);
        mainCircle.setCenterY(this.cyclePane.getHeight() / 2 + BEAT_CIRCLE_RADIUS);
        mainCircle.setStroke(Color.BLACK);
        mainCircle.setStrokeWidth(1);
        mainCircle.getStrokeDashArray().addAll(10.0, 10.0);

        this.cycle.clearAllBeats();
        this.beatCircles.clear();
        double angleStep = (2 * Math.PI) / this.cycle.getNbSlots().get();
        for (int i = 0; i < this.cycle.getNbSlots().get(); i++) {
            double angle = (i * angleStep) - (Math.PI / 2);
            Beat beat = new Beat(Defaults.NEW_BEAT_NAME + (i+1), angle);
            Circle c = new Circle(BEAT_CIRCLE_RADIUS, Color.rgb(255, 200, 60));
            c.setCenterX(this.cyclePane.getWidth() / 2);
            c.setCenterY(this.cyclePane.getHeight() / 2);
            c.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                public void handle(MouseEvent e) {
                    beatConfPane.setBeat(beat);
                }
            });
            this.beatCircles.add(c);
            this.cycle.addBeat(beat);
        }

        this.cyclePane.getChildren().addAll(mainCircle);
        this.cyclePane.getChildren().addAll(this.beatCircles);

        this.needle = new ImageView(new Image(CycleTab.class.getResourceAsStream("/needle.png")));
        this.cyclePane.getChildren().add(needle);
    }

    private void redrawCycle(){
        this.redrawCycle(null);
    }

    public void redrawCycle(double height){
        this.lastStageHeight = height;
        redrawCycle();
    }
    public void redrawCycle(Stage stage) {
        double menuHeight = menuPane.getPrefHeight();
        lastStageHeight = stage == null ? lastStageHeight : stage.getHeight();
        double height = lastStageHeight - menuHeight;
        this.cyclePane.setPrefHeight(height);

        double minRadius = (cyclePane.getWidth() > cyclePane.getHeight() ? cyclePane.getHeight() : cyclePane.getWidth()) / 2;
        double realRadius = minRadius - BEAT_CIRCLE_RADIUS - Defaults.PADDING;

        double xCenter = cyclePane.getWidth() / 2;
        double yCenter = cyclePane.getHeight() / 2;

        mainCircle.radiusProperty().setValue(realRadius);
        mainCircle.centerXProperty().setValue(xCenter);
        mainCircle.centerYProperty().setValue(yCenter);

        double angleStep = (2 * Math.PI) / this.beatCircles.size();

        int i = 0;
        for (Circle c : this.beatCircles) {
            c.centerXProperty().setValue(xCenter + (mainCircle.radiusProperty().get() * Math.cos(this.cycle.getBeat(i).getAngle().get())));
            c.centerYProperty().setValue(yCenter + (mainCircle.radiusProperty().get() * Math.sin(this.cycle.getBeat(i).getAngle().get())));

            i++;
        }

        this.needle.setFitHeight(realRadius);
        this.needle.setPreserveRatio(true);
        this.needle.xProperty().setValue(xCenter);
        this.needle.yProperty().setValue(yCenter - this.needle.getBoundsInParent().getHeight());
    }

}
