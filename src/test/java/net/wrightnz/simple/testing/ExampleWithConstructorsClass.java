package net.wrightnz.simple.testing;

import java.awt.Point;

/**
 *
 * @author Richard Wright
 */
public class ExampleWithConstructorsClass {

    private final Point startingPoint;

    public ExampleWithConstructorsClass() {
        this.startingPoint = new Point(0, 0);
    }

    public ExampleWithConstructorsClass(Point startingPoint) {
        this.startingPoint = startingPoint;
    }

    public Point zeroPoint(Point p) {
        p.setLocation(new Point(0, 0));
        return p;
    }

    public Point zeroPoint() {
        startingPoint.setLocation(0, 0);
        return startingPoint;
    }

}
