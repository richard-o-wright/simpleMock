package net.wrightnz.simple.testing;

import org.apache.bcel.generic.Type;

import java.util.HashMap;
import java.util.Map;

public final class MockConsts {

  public static final String CONSTRUCTOR_METHOD_NAME = "<init>";

  public static final Map<Type, Object> TYPE_2_DEFAULT_VALUE = new HashMap<>();

  static {
    TYPE_2_DEFAULT_VALUE.put(Type.INT, 0);
    TYPE_2_DEFAULT_VALUE.put(Type.DOUBLE, 0.0D);
    TYPE_2_DEFAULT_VALUE.put(Type.CHAR, (char)0);
    TYPE_2_DEFAULT_VALUE.put(Type.FLOAT, 0.0F);
    TYPE_2_DEFAULT_VALUE.put(Type.BYTE, Byte.MIN_VALUE);
    TYPE_2_DEFAULT_VALUE.put(Type.LONG, 0L);
    TYPE_2_DEFAULT_VALUE.put(Type.BOOLEAN, Boolean.FALSE);
    TYPE_2_DEFAULT_VALUE.put(Type.OBJECT, new Object());
    TYPE_2_DEFAULT_VALUE.put(Type.STRING, "");
    TYPE_2_DEFAULT_VALUE.put(Type.SHORT, 0);
    TYPE_2_DEFAULT_VALUE.put(Type.STRINGBUFFER, null);
    TYPE_2_DEFAULT_VALUE.put(Type.THROWABLE, null);
  }

  private MockConsts() {}

}
