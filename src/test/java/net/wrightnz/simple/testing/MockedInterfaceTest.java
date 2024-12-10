package net.wrightnz.simple.testing;

import static net.wrightnz.simple.testing.SimpleMocker.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Richard Wright
 */
class MockedInterfaceTest {

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
    // Mock a method
    MockMethod<String> meth1 = new MockMethod<>("Fish", "doSomething", int.class, String.class);
    // Mock an Interface
    ExampleInterface example = mock(ExampleInterface.class, meth1);
    // Call the mocked method on the mocked interface.
    example.doSomething(1, "");
    String result = example.doSomething(1, "test");
    // Check the mock method was called n number of times.
    assertEquals(2, meth1.getInvocationCount());
    // Check the expected mock result was also returned.
    assertEquals("Fish", result);
  }

  @Test
  void testReturnMock() {
    // Mock a method
    MockMethod<String> getAnswer = new MockMethod<>("expected", "getAnswer");
    Returned returned = mock(Returned.class, getAnswer);

    MockMethod<Returned> getReturned = new MockMethod<>(returned, "getReturned");
    // Mock an Interface
    ExampleInterface example = mock(ExampleInterface.class, getReturned);
    // Call the mocked method on the mocked interface.
    Returned actual = example.getReturned();
    
    assertTrue(actual.getClass().getName().contains(".$Proxy"));
    assertEquals("expected", actual.getAnswer());
  }

}
