package net.wrightnz.simple.testing;

import java.awt.Point;

/**
 * @author Richard Wright
 */
public class ExampleWithConstructorsClass {

  private Point startingPoint;
  private final Point endPoint;

  public ExampleWithConstructorsClass(int x, long y, String str, float fl, boolean isTest, Point point) {
    this.startingPoint = new Point(x, (int) y);
    this.endPoint = point;
  }

  public ExampleWithConstructorsClass(Point startingPoint, Point endPoint) {
    this.startingPoint = startingPoint;
    this.endPoint = endPoint;
  }

  public ExampleClass getExample(int i) {
    return new ExampleClass();
  }

  public ExampleWithNoNullConstructorClass getExample2() {
    return new ExampleWithNoNullConstructorClass("", "");
  }

  public Point zeroPoint() {
    startingPoint.setLocation(0, 0);
    return startingPoint;
  }

  public Point getEndPoint() {
    return endPoint;
  }

  public Point addStartX(int amount) {
    startingPoint = new Point(startingPoint.x + amount, startingPoint.y);
    return startingPoint;
  }

  public int getStartX() {
    return endPoint.x;
  }

  public int getStartY() {
    return endPoint.y;
  }

}
