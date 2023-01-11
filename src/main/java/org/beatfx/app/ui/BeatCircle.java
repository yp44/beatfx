package org.beatfx.app.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.beatfx.app.model.Beat;
import org.beatfx.app.util.Defaults;

public class BeatCircle extends Pane {

    private final Beat beat;
    private final Circle circle;

    private final Text label;

    private final double radius;

    public BeatCircle(double radius, Paint color, Beat beat) {
        super();
        this.radius = radius;
        this.beat = beat;
        this.circle = new Circle(Defaults.BEAT_CIRCLE_RADIUS, Color.rgb(255, 200, 60));
        label = new Text();
        buildUI();
    }

    public final void setXProperty(double x) {
        super.layoutXProperty().setValue(x - this.radius);
    }

    public final void setYProperty(double y) {
        super.layoutYProperty().setValue(y - this.radius);
    }

    private void buildUI() {
        this.setMaxHeight(this.radius * 2);
        this.setMaxWidth(this.radius * 2);

        this.circle.setCenterX(radius);
        this.circle.setCenterY(radius);

        label.setFont(Font.font(Defaults.BEAT_FONT_SIZE));
        label.setStyle("-fx-fill: " + Defaults.BEAT_LABEL_COLOR);
        label.textProperty().bindBidirectional(beat.getId());
        label.setY(radius + (label.getLayoutBounds().getHeight() / 4));
        label.setX(radius - (label.getLayoutBounds().getWidth() / 2));
        label.boundsInLocalProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observableValue, Bounds oldBounds, Bounds newBounds) {
                label.xProperty().setValue(radius - (newBounds.getWidth() / 2));
            }
        });
        this.getChildren().addAll(this.circle, label);
    }

}
