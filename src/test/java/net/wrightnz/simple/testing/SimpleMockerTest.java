package net.wrightnz.simple.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author richard
 */
class SimpleMockerTest {

  @Test
  void mock() {
    ExampleInterface example = SimpleMocker.mock(ExampleInterface.class);
    String result = example.doSomething(1, "foo");
    assertNull(result);
  }

  @Test
  void testMock() {
    String expected = "Expected";
    Map<String, Object> responses = new HashMap<>();
    responses.put("doSomething", expected);
    ExampleInterface example = SimpleMocker.mock(ExampleInterface.class, responses);
    String result = example.doSomething(1, "foo");
    assertEquals(expected, result);
  }

    @Test
    void testMockMethods() {
        String expected = "Fish";
        // Mock a method
        MockMethod<String> meth1 = new MockMethod<>("doSomething", expected);
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

}