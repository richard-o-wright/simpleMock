package net.wrightnz.simple.testing;

/**
 * @author Richard Wright
 */
public class ExampleClass {

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

    /* public Object getObject() {
        return new Object();
    } */

}
