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

    @Test
    void testMockClass() {
        // Mock an Class
        ExampleClass example = SimpleMocker.mock(ExampleClass.class);
        // Check the expected mock result was also returned.
        assertEquals(0, example.zero(10));
        assertEquals("-", example.getString("1"));
        assertEquals(0.0D, example.getDouble("1"));
    }


    @Test
    void testMockClassWithMockMethods() {
        String expected = "Fish";
        // Mock a method
        MockMethod<String> meth1 = new MockMethod<>("getString", expected);
        // Mock an Class
        ExampleClass example = SimpleMocker.mock(ExampleClass.class, meth1);
        // Check the expected mock result was also returned.
        assertEquals(0, example.zero(10));
        assertEquals(expected, example.getString("1"));
        assertEquals(0.0D, example.getDouble("1"));
    }

    // @Test
    void testMockClassWithContructorsMethods() {
        Point expected = new Point(0, 0);
        // Mock an Class
        ExampleWithConstructorsClass example = SimpleMocker.mock(ExampleWithConstructorsClass.class);
        // Call the mocked method on the mocked interface.
        Point result = example.zeroPoint(new Point(10, 10));

        // Check the expected mock result was also returned.
        assertEquals(expected, result);
    }

}
