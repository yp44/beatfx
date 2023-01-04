package org.beatfx.fx.widgets;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.beatfx.config.Beat;
import org.beatfx.config.Cycle;
import org.beatfx.fx.BeatFXApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CycleTab extends Tab {

    private final static int BEAT_CIRCLE_RADIUS = 40;

    private final Cycle cycleConf = new Cycle();

    private HBox menuPane;

    private Pane cyclePane;

    private BeatPane beatConfPane;

    private Circle mainCircle;

    private ImageView needle;

    private List<Circle> beatCircles = new ArrayList<>();

    public CycleTab(Stage stage, String name) {
        super(name);
        cycleConf.getId().setValue(name);
        buildUI(stage);
    }

    public Cycle getCycleConf() {
        return cycleConf;
    }

    private void buildUI(final Stage stage) {
        this.menuPane = buildMenuPane();
        this.cyclePane = buildCyclePane();
        this.beatConfPane = new BeatPane();



        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().addAll(this.cyclePane, this.beatConfPane);
        splitPane.getDividers().get(0).positionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                redrawCycle(stage);
            }
        });


        VBox mainPane = new VBox();
        mainPane.getChildren().addAll(menuPane, splitPane);

        this.setContent(mainPane);
    }

    private HBox buildMenuPane() {
        HBox menu = new HBox();
        menu.setPadding(new Insets(BeatFXApp.PADDING, BeatFXApp.PADDING, BeatFXApp.PADDING, BeatFXApp.PADDING));
        menu.setSpacing(BeatFXApp.PADDING);
        Spinner<Integer> nbSlotsCtrl = new Spinner<>(1, 100, cycleConf.getNbSlots().get());
        nbSlotsCtrl.getValueFactory().valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer oldInt, Integer newInt) {
                cycleConf.getNbSlots().setValue(newInt);
            }
        });

        Spinner<Integer> durationCtrl = new Spinner<>(1, 3600000, cycleConf.getDuration().get());
        nbSlotsCtrl.getValueFactory().valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer oldInt, Integer newInt) {
                cycleConf.getDuration().setValue(newInt);
            }
        });

        Spinner<Integer> nbLoopCtrl = new Spinner<>(0, 10000, cycleConf.getNbLoop().get());
        nbLoopCtrl.getValueFactory().valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer oldInt, Integer newInt) {
                cycleConf.getNbLoop().setValue(newInt);
            }
        });

        TextField nextCtrl = new TextField(cycleConf.getNextId().get());
        nextCtrl.textProperty().bindBidirectional(cycleConf.getNextId());


        menu.getChildren().addAll(new Label("Number of slots"),
                nbSlotsCtrl,
                new Label("Duration"),
                durationCtrl,
                new Label("Loop"),
                nbLoopCtrl,
                new Label("Next cycle"),
                nextCtrl);

        return menu;
    }

    private Pane buildCyclePane() {
        Pane cycle = new Pane();

        //cycle.setBackground(new Background(new BackgroundFill(new Color(0.7, 0.5, 0.5, 1), new CornerRadii(0), new Insets(0, 0, 0, 0))));
        cycle.setMaxHeight(Double.MAX_VALUE);

        mainCircle = new Circle(100, Color.TRANSPARENT);
        mainCircle.setCenterX(cycle.getWidth() / 2);
        mainCircle.setCenterY(cycle.getHeight() / 2 + BEAT_CIRCLE_RADIUS);
        mainCircle.setStroke(Color.BLACK);
        mainCircle.setStrokeWidth(1);
        mainCircle.getStrokeDashArray().addAll(10.0, 10.0);

        this.cycleConf.getBeats().clear();
        for (int i = 0; i < this.cycleConf.getNbSlots().get(); i++) {
            Beat beat = new Beat("Beat #"+(i+1));
            Circle c = new Circle(BEAT_CIRCLE_RADIUS, Color.rgb(255, 200, 60));
            c.setCenterX(cycle.getWidth() / 2);
            c.setCenterY(cycle.getHeight() / 2);
            c.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
                    public void handle(MouseEvent e){
                        beatConfPane.setBeat(beat);
                    }
            });
            this.beatCircles.add(c);
            this.cycleConf.getBeats().add(beat);
        }

        cycle.getChildren().addAll(mainCircle);
        cycle.getChildren().addAll(this.beatCircles);

        this.needle = new ImageView(new Image(CycleTab.class.getResourceAsStream("/needle.png")));
        cycle.getChildren().add(needle);

        return cycle;
    }

    public void redrawCycle(Stage stage) {
        double menuHeight = menuPane.getPrefHeight();
        double height = stage.getHeight() - menuHeight;
        this.cyclePane.setPrefHeight(height);

        double minRadius = (cyclePane.getWidth() > cyclePane.getHeight() ? cyclePane.getHeight() : cyclePane.getWidth()) / 2;
        double realRadius = minRadius - BEAT_CIRCLE_RADIUS - BeatFXApp.PADDING;

        double xCenter = cyclePane.getWidth() / 2;
        double yCenter = cyclePane.getHeight() / 2;

        mainCircle.radiusProperty().setValue(realRadius);
        mainCircle.centerXProperty().setValue(xCenter);
        mainCircle.centerYProperty().setValue(yCenter);

        double angleStep = (2 * Math.PI) / this.beatCircles.size();

        int i = -2;
        for(Circle c : this.beatCircles){
            c.centerXProperty().setValue(xCenter + (mainCircle.radiusProperty().get() * Math.cos(angleStep * i)));
            c.centerYProperty().setValue(yCenter + (mainCircle.radiusProperty().get() * Math.sin(angleStep * i)));
            i++;
        }

        /*double needleH = this.needle.getImage().getHeight();
        double needleW = this.needle.getImage().getWidth();
        double scaleH = needleH / minRadius;*/

        this.needle.setFitHeight(realRadius);
        this.needle.setPreserveRatio(true);
        this.needle.xProperty().setValue(xCenter);
        this.needle.yProperty().setValue(yCenter - this.needle.getBoundsInParent().getHeight());
    }

}
