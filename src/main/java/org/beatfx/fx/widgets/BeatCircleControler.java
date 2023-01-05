package org.beatfx.fx.widgets;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class BeatCircleControler extends Circle {

    private double angle;

    public BeatCircleControler(double radius, Paint paint, double angle){
        super(radius, paint);
        this.angle = angle;
    }

    public double getAngle(){
        return this.angle;
    }

}
