package net.wrightnz.simple.testing;

import static net.wrightnz.simple.testing.SimpleMocker.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import java.awt.*;

/**
 *
 * @author Richard Wright
 */
class MockedClassTest {

  @Test
  void testMockClass() {
    // Mock a Class
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
    MockMethod<Void> addCharToStr1 = new MockMethod<>(null, "addCharToStr1", char.class);
    MockMethod<Float> getFloatObj = new MockMethod<>(Float.MAX_VALUE, "getFloatObj");
    MockMethod<Boolean> getBooleanObj = new MockMethod<>(Boolean.TRUE, "getBooleanObj");
    // Mock an Class
    ExampleClass mock = mock(ExampleClass.class, getInt, getString, getChar, getDouble, getByte, getBoolean, addCharToStr1, getFloatObj, getBooleanObj);
    // Check the expected mock result was also returned.
    assertEquals(42, mock.getInt(10));
    assertEquals(expectedStr, mock.getString("1"));
    assertEquals(expectedDouble, mock.getDouble("1"));
    assertEquals(expectedChar, mock.getChar());
    assertEquals(expectedByte, mock.getByte("", ""));
    assertEquals(Boolean.TRUE, mock.getBoolean());
    assertNull(mock.getObject());
    assertEquals(Float.MAX_VALUE, mock.getFloatObj());
    assertEquals(Boolean.TRUE, mock.getBooleanObj());
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
    // Call the mocked method on the mocked class.
    ExampleClass actual = example.getExample(10);
    // Check the expected mock result was also returned.
    assertEquals(expected.getBoolean(), actual.getBoolean());
  }

  @Test
  void testNonNullConstructorReturn() {
    ExampleWithoutNullConstructor expected = new ExampleWithoutNullConstructor("1", "2");
    // Mock methods
    MockMethod<ExampleWithoutNullConstructor> getExample2 = new MockMethod<>(expected, "getExample2");
    // Mock the Class
    ExampleWithConstructorsClass example = mock(ExampleWithConstructorsClass.class, getExample2);
    // Call the mocked method on the mocked interface.
    ExampleWithoutNullConstructor actual = example.getExample2();
    // Then
    assertEquals("", actual.getStr1());
  }

  @Test
  void testMockNonNullConstructorReturn() {
    MockMethod<String> getAnswer = new MockMethod<>("-", "getAnswer");
    SimpleExampleClass.ReturnedClass returned = mock(SimpleExampleClass.ReturnedClass.class, getAnswer);
    System.out.println(">>>>>>> " + returned.getClass());
    assertEquals("-", returned.getAnswer());

    // ToDo: Make this fancy recursive stuff work maybe? Or perhaps it's a bad idea and shouldn't be supported?
    // I mean having a mock return a mock implies a lot of assumptions are being made
    /*
    // Mock methods
    MockMethod<SimpleExampleClass.ReturnedClass> getReturned = new MockMethod<>(returned, "getReturned");
    // Mock the Class
    SimpleExampleClass example = mock(SimpleExampleClass.class, getReturned);
    System.out.println(">>>>>>> " + example.getClass());
    // Call the mocked method on the mocked class.
    SimpleExampleClass.ReturnedClass actual = example.getReturned();
    // Then
    assertEquals("-", actual.getAnswer());
     */
  }

}
