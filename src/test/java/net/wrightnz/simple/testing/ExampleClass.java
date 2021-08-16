package net.wrightnz.simple.testing;

import java.awt.*;

/**
 * @author Richard Wright
 */
public class ExampleClass {

    private String str1;
    private String str2;

    public ExampleClass(String str1, String str2){
        this.str1 = str1;
        this.str2 = str2;
    }

    public ExampleClass(){
    }

    public boolean getBoolean() {
        return Boolean.TRUE;
    }

    public byte getByte(String str1, String str2) {
        return Byte.MAX_VALUE;
    }

    public char getChar() {
        return 'a';
    }

    public int getInt(int i) {
        return Integer.MAX_VALUE;
    }

    public long getLong(Object obj) {
        return Long.MAX_VALUE;
    }

    public float getFloat(String str) {
        return 0.0F;
    }

    public double getDouble(String str) {
        return 0.0;
    }

    public String getString(String str) {
        return "test-" + str;
    }

    public Object getObject() {
        return new Object();
    }

    public Point zeroPoint(Point p) {
        p.setLocation(new Point(0, 0));
        return p;
    }

}
