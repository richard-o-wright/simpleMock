package net.wrightnz.simple.testing;

import java.awt.Point;

/**
 *
 * @author Richard Wright
 */
public class ExampleWithConstructorClass {

    private final Point startingPoint;

    public ExampleWithConstructorClass(Point startingPoint) {
        this.startingPoint = startingPoint;
    }

    public Point zeroPoint() {
        startingPoint.setLocation(0, 0);
        return startingPoint;
    }

}
