package net.wrightnz.simple.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static net.wrightnz.simple.testing.SimpleMocker.mock;

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
  void testSimplestMock() {
    ExampleInterface example = mock(ExampleInterface.class);
    String result = example.doSomething();
    assertNull(result);
  }

  @Test
  void testMock() {
    String expected = "Expected";
    Map<String, Object> responses = new HashMap<>();
    responses.put("doSomething", expected);
    ExampleInterface example = mock(ExampleInterface.class, responses);
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
    ExampleInterface example = mock(ExampleInterface.class, meth1);
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
    ExampleClass example = mock(ExampleClass.class);
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
    ExampleClass actual = mock(ExampleClass.class, getInt, getString, getChar, getDouble, getByte, getBoolean);
    // Check the expected mock result was also returned.
    assertEquals(42, actual.getInt(10));
    assertEquals(expectedStr, actual.getString("1"));
    assertEquals(expectedDouble, actual.getDouble("1"));
    assertEquals(expectedChar, actual.getChar());
    assertEquals(expectedByte, actual.getByte("", ""));
    assertEquals(Boolean.TRUE, actual.getBoolean());
    assertEquals(null, actual.getObject());
  }

  @Test
  void testMockClassWithConstructorsMethods() {
    int expected = 10;
    // Mock methods
    MockMethod<Integer> getStartX = new MockMethod<>(expected, "getStartX");
    // Mock the Class
    ExampleWithConstructorsClass example = mock(ExampleWithConstructorsClass.class, getStartX);
    // Call the mocked method on the mocked interface.
    Point actual = example.getEndPoint();
    // Check the expected mock result was also returned.
    assertEquals(null, actual);
    assertEquals(expected, example.getStartX());
  }

  @Test
  void testMockNonRtObjectReturned() {
    ExampleClass expected = new ExampleClass();
    // Mock methods
    MockMethod<ExampleClass> getExample = new MockMethod<>(expected, "getExample", int.class);
    // Mock the Class
    ExampleWithConstructorsClass example = mock(ExampleWithConstructorsClass.class, getExample);
    // Call the mocked method on the mocked interface.
    ExampleClass actual = example.getExample(10);
    // Check the expected mock result was also returned.
    assertEquals(expected.getBoolean(), actual.getBoolean());
  }

  @Test
  void testNonNullConstructorReturn() {
    ExampleWithoutNullConstructor expected = new ExampleWithoutNullConstructor("", "");
    // Mock methods
    MockMethod<ExampleWithoutNullConstructor> getExample2 = new MockMethod<>(expected, "getExample2");
    // Mock the Class
    ExampleWithConstructorsClass example = mock(ExampleWithConstructorsClass.class, getExample2);
    // Call the mocked method on the mocked interface.
    ExampleWithoutNullConstructor actual = example.getExample2();
    // Then
    assertEquals(expected.getStr1(), actual.getStr1());
  }

  @Test
  void testMockNonNullConstructorReturn() {
    MockMethod<String> getStr1 = new MockMethod<>("-", "getStr1");
    ExampleWithoutNullConstructor returned = mock(ExampleWithoutNullConstructor.class, getStr1);
    assertEquals("-", returned.getStr1());

    // Mock methods
    MockMethod<ExampleWithoutNullConstructor> getExample2 = new MockMethod<>(returned, "getExample2");
    // Mock the Class
    ExampleWithConstructorsClass example = mock(ExampleWithConstructorsClass.class, getExample2);
    // Call the mocked method on the mocked interface.
    ExampleWithoutNullConstructor actual = example.getExample2();
    // Then
    // ToDo: Make this fancy recursive stuff work maybe or
    //  perhaps it's a bad idea and shouldn't be supported?
    assertEquals(null, actual);
  }

}
