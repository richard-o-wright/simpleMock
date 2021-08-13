package net.wrightnz.simple.testing;

import java.awt.Point;

/**
 * @author Richard Wright
 */
public class ExampleWithConstructorsClass {

    private final Point startingPoint;

    public ExampleWithConstructorsClass(int x, long y, String str, float fl, boolean isTest) {
        this.startingPoint = new Point(x, (int) y);
    }

    public ExampleWithConstructorsClass(Point startingPoint) {
        this.startingPoint = startingPoint;
    }

    public Point zeroPoint() {
        startingPoint.setLocation(0, 0);
        return startingPoint;
    }

}
