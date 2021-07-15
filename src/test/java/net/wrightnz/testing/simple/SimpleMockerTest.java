package net.wrightnz.testing.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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
}