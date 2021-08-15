package net.wrightnz.simple.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Richard Wright
 */
class SimpleMockerTest {

  @Test
  void mock() {
    ExampleInterface example = SimpleMocker.mock(ExampleInterface.class);
    String result = example.doSomething();
    assertNull(result);
  }

  @Test
  void testMock() {
    String expected = "Expected";
    Map<String, Object> responses = new HashMap<>();
    responses.put("doSomething", expected);
    ExampleInterface example = SimpleMocker.mock(ExampleInterface.class, responses);
    String result = example.doSomething();
    assertEquals(expected, result);
  }

  @Test
  void testMockMethods() {
    String expected = "Fish";

    Class<?>[] doSomethingTypes = {int.class, String.class};
    // Mock a method
    MockMethod<String> meth1 = new MockMethod<>(expected, "doSomething", doSomethingTypes);
    // Mock an Interface
    ExampleInterface example = SimpleMocker.mock(ExampleInterface.class, meth1);
    // Call the mocked method on the mocked interface.
    example.doSomething(1, "");
    String result = example.doSomething(1, "test");
    // Check the mock method was called n number of times.
    assertEquals(2, meth1.getInvocationCount());
    // Check the expected mock result was also returned.
    assertEquals(expected, result);
  }

  @Test
  void testMockClass() {
    // Mock an Class
    ExampleClass example = SimpleMocker.mock(ExampleClass.class);
    // Check the expected mock result was also returned.
    assertEquals(0, example.getInt(10));
    assertEquals("", example.getString("1"));
    assertEquals(0.0D, example.getDouble("1"));
    assertEquals(null, example.getObject());
  }


  @Test
  void testMockClassWithMockMethods() {
    String expectedStr = "Fish";
    char expectedChar = 'z';
    double expectedDouble = 3.14234;
    byte expectedByte = 7;
    // Mock a method
    MockMethod<Integer> getInt = new MockMethod<>(42, "getInt", int.class);
    MockMethod<String> getString = new MockMethod<>(expectedStr, "getString", String.class);
    MockMethod<Character> getChar = new MockMethod<>(expectedChar, "getChar");
    MockMethod<Double> getDouble = new MockMethod<>(expectedDouble, "getDouble", String.class);
    MockMethod<Byte> getByte = new MockMethod<>(expectedByte, "getByte", String.class, String.class);
    MockMethod<Boolean> getBoolean = new MockMethod<>(Boolean.TRUE, "getBoolean");
    // Mock an Class
    ExampleClass example = SimpleMocker.mock(ExampleClass.class, getInt, getString, getChar, getDouble, getByte, getBoolean);
    // Check the expected mock result was also returned.
    assertEquals(42, example.getInt(10));
    assertEquals(expectedStr, example.getString("1"));
    assertEquals(expectedDouble, example.getDouble("1"));
    assertEquals(expectedChar, example.getChar());
    assertEquals(expectedByte, example.getByte("", ""));
    assertEquals(Boolean.TRUE, example.getBoolean());
    assertEquals(null, example.getObject());
  }

  @Test
  void testMockClassWithContructorsMethods() {
    int expected = 10;
    // Mock methods
    MockMethod<Integer> getStartX = new MockMethod<>(expected, "getStartX");
    // Mock the Class
    ExampleWithConstructorsClass example = SimpleMocker.mock(ExampleWithConstructorsClass.class, getStartX);
    // Call the mocked method on the mocked interface.
    Point result = example.getEndPoint();
    // Check the expected mock result was also returned.
    assertEquals(null, result);
    assertEquals(expected, example.getStartX());
  }

  @Test
  void testMockObjectReturned() {
    Point expected = new Point(2, 4);
    // Mock methods
    MockMethod<Point> addStartX = new MockMethod<>(expected, "addStartX", int.class);
    // Mock the Class
    ExampleWithConstructorsClass example = SimpleMocker.mock(ExampleWithConstructorsClass.class, addStartX);
    // Call the mocked method on the mocked interface.
    Point result = example.addStartX(10);
    // Check the expected mock result was also returned.
    assertEquals(expected, result);
  }
  
}
